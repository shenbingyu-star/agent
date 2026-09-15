package com.example.demo.config;

import com.example.demo.service.TooolsService;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.*;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.apache.logging.log4j.message.Message;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.StreamTokenizer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-16
 * @Description: 配置类
 * @Version: 1.0
 */
@Configuration
public class AiConfig {

    // TODO  实例化一个向量数据库到spring容器
    @Bean
    public EmbeddingStore embeddingStore() {
        return new InMemoryEmbeddingStore();
    }

    //TODO 设置RAG知识库 将文本内容读取到向量数据库中
    @Bean
    CommandLineRunner insertTermsOfServiceToVectorStore(
            QwenEmbeddingModel qwenEmbeddingModel,
            EmbeddingStore embeddingStore
    ) throws Exception {
//读取文本
        Path path = Paths.get(getClass().getClassLoader().getResource("rag/terms-of-service.txt").toURI());
        //将内容读取到向量数据库中
        return args -> {
            //读取文本
            TextDocumentParser documentParser = new TextDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(path, documentParser);
            //文本分词
            DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(200, 50);
            List<TextSegment> segments = splitter.split(document);

            //文本向量化
            Response<List<Embedding>> response = qwenEmbeddingModel.embedAll(segments);
            List<Embedding> content = response.content();

            //将向量化结果存放到向量数据库中
            embeddingStore.addAll(content, segments);//将向量化文本和分割好的文本一起放到向量数据库中

        };



    }


    //设置代理的类 内部接口
    public interface Assistant {
        //声明代理方法
        //普通聊天方法（阻塞式，返回完整响应）
        String chat(String message);

        //定义角色
        @SystemMessage("""
                您是“12345”铁路公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                您正在通过在线聊天系统与客户互动。
                在提供有关预订或取消预订的信息之前，您必须始终从用户处获取以下信息:车的班号、客户姓名、客户身份证。
                请讲中文。
                今天的日期是 {{current_date}}.
                """)
        // 流式响应方法（实时返回生成的token）

        TokenStream stream(@UserMessage String message, @V("current_date") String currentDate);
    }

    @Bean
//    使用Assistant类方法实现代理
    public Assistant assistant(
            // 注入标准聊天语言模型（用于普通响应）
            ChatLanguageModel chatLanguageModel,
            // 注入流式聊天语言模型（用于流式响应）
            StreamingChatLanguageModel streamingChatLanguageModel,
            //添加特定的业务逻辑，遇到这类问题时会优先调用设计好的对应函数
            TooolsService TooolsService,
            //TODO注入千问向量模型 注意配置spring框架
            QwenEmbeddingModel qwenEmbeddingModel,

            //TODO注入向量数据库
            EmbeddingStore embeddingStore

    ) {//聊天对象作为参数进来
//创建基于内存存储聊天记忆的对象 保留100条聊天记忆
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(100);

        //TODO 设置内容检索器 做匹配规则
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(qwenEmbeddingModel)//导入千问模型
                .embeddingStore(embeddingStore)//导入向量数据库
                .maxResults(8)
                .minScore(0.6)
                .build();


//使用AIServices来构建实现服务
        Assistant assistant = AiServices.builder(Assistant.class)
                // 设置标准聊天模型（用于chat()方法）
                .chatLanguageModel(chatLanguageModel)//设置聊天模型
                // 设置流式聊天模型（用于stream()方法）
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .tools(TooolsService)//识别到特定问题时调用
                // 配置聊天记忆保持对话上下文
                .chatMemory(memory)//设置聊天记忆对象
                //TODO将内容检索器设置到代理对象中
                .contentRetriever(contentRetriever)
                .build();
        return assistant;//返回通代理模式实现的Assistant对象
    }

    //    创建一个新的代理 用id来区分用户
    public interface AssistantUnique {
        //普通聊天方法(阻塞式 返回完整响应
        String chat(@UserMessage String message, @MemoryId int userId);
//流式聊天方法实时返回Token

        /**
         * 流式聊天方法（实时返回token）
         *
         * @param memoryId    对话记忆ID（用于区分不同会话）
         * @param userMessage 用户输入的消息
         * @return TokenStream 流式响应对象
         */

        TokenStream stream(@UserMessage String message, @MemoryId int userId);

    }

    @Bean
    public AssistantUnique assistantUnique(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel,
            ChatMemoryStore chatMemoryStore
    ) {

        AssistantUnique uniqueAiServices = AiServices.builder(AssistantUnique.class)
                //同步聊天模型
                .chatLanguageModel(chatLanguageModel)
//                设置流式聊天模式
                .streamingChatLanguageModel(streamingChatLanguageModel)
//                配置对话记忆提供者
                .chatMemoryProvider(MemoryId ->
                        MessageWindowChatMemory.builder()
                                .maxMessages(100)//每个对话最多保留10条消息
                                .chatMemoryStore(chatMemoryStore)
                                .id(MemoryId)//设置记忆id
                                .build())
                .build();

        return uniqueAiServices;
    }


//    public interface AssiatantUnique1 {
//        String chat(@UserMessage String message, @MemoryId int userId);
//
//        TokenStream stream(@UserMessage String message, @MemoryId int userId);
//    }
//
//    @Bean
//    public AssiatantUnique1 assiatantUnique1(
//            ChatLanguageModel chatLanguageModel,
//            StreamingChatLanguageModel streamingChatLanguageModel
//    ) {
//        AssiatantUnique1 assiatantUnique1AiServices = AiServices.builder(AssiatantUnique1.class)
//                .chatLanguageModel(chatLanguageModel)
//                .streamingChatLanguageModel(streamingChatLanguageModel)
//                .chatMemoryProvider(MemoryId ->
//                        MessageWindowChatMemory.builder()
//                                .maxMessages(100)
//                                .id(MemoryId)
//                                .build())
//                .build();
//        return assiatantUnique1AiServices;
//    }
























    // TODO  实例化一个向量数据库到spring容器
//    @Bean
//    public EmbeddingStore embeddingStore1(){
//        return new InMemoryEmbeddingStore();
//    }


    //TODO 设置RAG知识库 将文本内容读取到向量数据库中
//    @Bean
//    CommandLineRunner insertTermsOfServiceToVectorStore1 (
//            EmbeddingStore embeddingStore,
//            QwenEmbeddingModel qwenEmbeddingModel
//    )throws  Exception{
//        Path path = Paths.get(getClass().getClassLoader().getResource("rag/terms-of-service.txt").toURI());
//
//        return args -> {
//            TextDocumentParser documentParser = new TextDocumentParser();
//            Document document = FileSystemDocumentLoader.loadDocument(path, documentParser);
//            DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(200, 50);
//            List<TextSegment> segments = splitter.split(document);
//            Response<List<Embedding>> response = qwenEmbeddingModel.embedAll(segments);
//            List<Embedding> content = response.content();
//            embeddingStore.addAll(content, segments);//将向量化文本和分割好的文本一起放到向量数据库中
//        };
//    }


    //TODO 设置内容检索器 做匹配规则
//    @Bean
//    public Assistant assistant1(
//            EmbeddingStore embeddingStore,
//            QwenEmbeddingModel qwenEmbeddingModel
//    ){
//        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(qwenEmbeddingModel)
//                .maxResults(8)
//                .minScore(0.6)
//                .build();
//
//    }



}
