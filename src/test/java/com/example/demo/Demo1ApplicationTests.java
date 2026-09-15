package com.example.demo;

import com.example.demo.config.AiConfig;
import com.example.demo.entity.TicketOrder;
import com.example.demo.mapper.TicketOrderMapper;
import com.example.demo.service.TicketOrderService;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class Demo1ApplicationTests {

//    @Autowired
//    private QwenEmbeddingModel qwenEmbeddingModel;


    //    时间测试
    @Test
    void testTime() {
        Instant instant = Instant.now(); // 当前 UTC 时间
        LocalDateTime localDateTime = LocalDateTime.now(); // 当前系统默认时区的本地时间
        System.out.println("instant = " + instant);
        System.out.println("localDateTime = " + localDateTime);
    }

    @Test
    void contextLoads() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
//                .baseUrl()  默认
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String chat = chatModel.chat("你好，你在为谁服务？");
        System.out.println(chat);


    }

    @Test
        //使用langchain4j接入deepseek
    void testDeepSeek() {
//        创建模型对象
        OpenAiChatModel aichatModel = OpenAiChatModel.builder()
//                .baseUrl()  默认
                .baseUrl("https://api.deepseek.com/v1")//提供模型API地址
                .apiKey("sk-95836f2eea03402692d1bad4739beec0")//模型秘钥
                .modelName("deepseek-chat")//设置模型名称使用聊天模型
                .build();
//        进行对话
        String chat1 = aichatModel.chat("你好，你是谁啊？，你和langchain4j的区别是什么啊？");
        System.out.println(chat1);


    }

    @Test
    void abcDeepSeek() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey("sk-95836f2eea03402692d1bad4739beec0")
                .modelName("deepseek-chat")
                .build();
        String chat = chatModel.chat("你好，你是谁啊，你能做什么？");
        System.out.println(chat);

    }

    @Test
//    使用阿里万象模型生成图片
    void testImage() {
//        创建图生文的对象
        WanxImageModel build = WanxImageModel.builder()
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81")
                .modelName("wan2.5-t2i-preview")
                .build();
//        调用方法输入提示词 生成图片
        Response<Image> response = build.generate("生成早晨太阳升起的图片");
        System.out.println(response);
    }

    @Test
    void demoChat() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .modelName("deepseek-chat")
                .apiKey("sk-95836f2eea03402692d1bad4739beec0")
                .build();
//        进行对话
        System.out.println("你好我是李白");
        UserMessage userMessage1 = UserMessage.userMessage("我是李白");
//        得到响应结果
        ChatResponse response1 = chatModel.chat(userMessage1);
//        取出响应结果中的响应信息
        AiMessage aiMessage1 = response1.aiMessage();
        System.out.println(aiMessage1.text());

        //携带之前的聊天记录 继续询问
        System.out.println("我是谁啊");
        UserMessage userMessage2 = UserMessage.userMessage("我是谁啊");
        //如果要实现理解上下文 就需要将上下文的聊天记录一起携带给大模型
        ChatResponse response2 = chatModel.chat(userMessage1, aiMessage1, userMessage2);
//        第二次响应的内容
        AiMessage aiMessage2 = response2.aiMessage();
        System.out.println(aiMessage2.text());

    }

    @Test
    public void Enbed() {

//        通义千问模型
        QwenEmbeddingModel qwenEmbeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81")
                .build();
//        输入要进行向量化的文本
        Response<Embedding> embed = qwenEmbeddingModel.embed("你好，jsafdsjdsfcxnnnnnnnnnnnnn" +
                "dsfljdssssssss" +
                "sdfkdsjjjjjjsdf" +
                "d我是虾米呢！");
//        输出向量化的文本
        System.out.println(embed.content().toString());
//        输出向量化的长度
        System.out.println(embed.content().vector().length);
    }

    @Test
//    测试向量匹配代码
    public void testEmbeddingStore() {
        // ------------------------------------------ 向量嵌入（Embedding）阶段 ------------------------------------------

//        创建向量数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //创建向量模型
        QwenEmbeddingModel qwenEmbeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81")
                .build();


//        将文本一存入向量数据库
        TextSegment textSegment1 = TextSegment.from(" 预订航班:\n" +
                                                    "            - 通过我们的网站或移动应用程序预订。\n" +
                                                    "            - 预订时需要全额付款。\n" +
                                                    "            - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。\n");
        embeddingStore.add(qwenEmbeddingModel.embed(textSegment1).content(), textSegment1);


//        将文本二存入向量数据库
        TextSegment textSegment2 = TextSegment.from("  取消预订:\n" +
                                                    "            - 最晚在航班起飞前 48 小时取消。\n" +
                                                    "            - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。\n" +
                                                    "            - 退款将在 7 个工作日内处理。\n");
        embeddingStore.add(qwenEmbeddingModel.embed(textSegment2).content(), textSegment2);

        // ---------------------- 数据检索阶段 ----------------------

        // 将用户的查询语句"取消航班要多少钱"转换为向量
        Embedding content = qwenEmbeddingModel.embed("取消航班要多少钱").content();


        // 构建向量搜索请求
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(content)//设置要查询的向量
                .maxResults(1)//返回结果最大值为1
                .minScore(0.65)//匹配相似度的百分比
                .build();


        // 执行向量搜索
        //向向量数据库搜索要查询请求
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

        // 处理搜索结果
        if (result.matches().isEmpty())//如果结果为空
        {
            System.out.println("没有匹配结果！");
        } else {// 遍历所有匹配结果（这里由于maxResults=1，只会有一个结果）
            result.matches().forEach(match -> {
                        System.out.println("相似度：" + match.score());
                        System.out.println("匹配内容: " + match.embedded().text());
                    }

            );
        }

    }


    //RAG知识库导入
    @Test
    void testRAG() throws Exception {
        //提取文件路径
        Path path = Paths.get(Demo1ApplicationTests.class
                .getClassLoader()
                .getResource("rag/terms-of-service.txt").toURI());

//        设置文件解析器
        TextDocumentParser textDocumentParser = new TextDocumentParser();

//        设置文件加载器加载文件
        Document document = FileSystemDocumentLoader.loadDocument(path, textDocumentParser);


//        文件内容输出
//        System.out.println(document);

        //文件内容分割 分割字符20，重叠部分字符10
        //例如 今天天气很好 按3,2分割 今天天 天天气 天很好 气很好 这样分割
        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(200, 40);
        List<TextSegment> segments = splitter.split(document);
//        System.out.println(segments);

//        文件内容向量化
        //        创建向量模型 千文模型
        QwenEmbeddingModel qwenEmbeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81")
                .build();
        List<Embedding> content = qwenEmbeddingModel.embedAll(segments).content();


//        创建向量化数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(content, segments);//(向量化文本，文本)


//处理向量搜索请求 设置结果规则
        Response<Embedding> embed = qwenEmbeddingModel.embed("退费规则");
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(embed.content())//"退票费用"
                .maxResults(10)//获取前3个结果
                .minScore(0.6)//最小相似度 设置最低相似度阈值
                .build();

//        向量化搜索以及处理
        //从向量数据库中搜索请求
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
        if (result.matches().isEmpty()) {
            System.out.println("没有匹配结果！");

        } else {
            result.matches().forEach(match -> {
                System.out.println("匹配相似度：" + match.score());
                System.out.println("匹配内容：" + match.embedded().text());
            });
        }


        //大模型处理响应结果

//        创建聊天模型对象deepseek OpenAi聊天模型
        OpenAiChatModel aichatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")//提供模型API地址
                .apiKey("sk-95836f2eea03402692d1bad4739beec0")//模型秘钥
                .modelName("deepseek-chat")//设置模型名称使用聊天模型
                .build();

//        创建EmbeddingStoreContentRetriever封装携带向量模型的处理结果

        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)//模型向量数据库
                .embeddingModel(qwenEmbeddingModel)//模型调用
                .maxResults(10)
                .minScore(0.6)
                .build();


//        设置代理对象 调用模型对象
        AiConfig.Assistant assistant = AiServices.builder(AiConfig.Assistant.class)
                .chatLanguageModel(aichatModel)
                .contentRetriever(retriever)//将携带向量模型的部分设置到代理对象中
                .build();
        //使用代理对象调用聊天方法
        System.out.println("----------------------------使用LLM(large language Model)大语言模型重新组织语言------------------------------");
        System.out.println(assistant.chat("退费规则"));
//        System.out.println(assistant.chat("开车前9天退票，我能退回多少车费？"));


        //        创建EmbeddingStoreContentRetriever封装携带向量模型的处理结果

//        EmbeddingStoreContentRetriever retriever1 = EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(qwenEmbeddingModel)
//                .maxResults(10)
//                .minScore(0.6)
//                .build();


        //        设置代理对象 调用模型对象
//        AiConfig.Assistant assistant1 = AiServices.builder(AiConfig.Assistant.class)
//                .chatLanguageModel(aichatModel)
//                .contentRetriever(retriever1)
//                .build();


        //使用代理对象调用聊天方法
//        System.out.println(assistant1.chat("我75块钱买的车票，在3天前退票，我能退回多少车票？"));


    }

    @Autowired
    private TicketOrderMapper ticketOrderMapper;

    @Test
    void testMySQL() throws Exception {
//        框架方法调用mysql
        List<TicketOrder> ticketOrders = ticketOrderMapper.selectList(null);
        for (TicketOrder ticketOrder : ticketOrders) {
            System.out.println(ticketOrder);
        }

//
////        原生方法访问myqsl
////        加载mysql驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");
////        创建数据库连接
//        Connection connection = DriverManager.getConnection
//                ("jdbc:mysql://localhost:3306/ticket", "root", "123456");
//
////        创建执行mysql语句的对象
//        Statement statement = connection.createStatement();
//
////        执行mysql语句
//        ResultSet resultSet = statement.executeQuery("select * from ticket_order");
//
////        处理结果
//        while (resultSet.next()){
////            System.out.println(resultSet.getString("order_number")
////            + " " + resultSet.getString("user_name"));
//
//
//            TicketOrder ticketOrder = new TicketOrder();
//            ticketOrder.setUserName(resultSet.getString("order_number"));
//            ticketOrder.setOrderNumber(resultSet.getString("user_name"));
//            System.out.println(ticketOrder);
//
//        }


//
//Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection connection1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ticket", "root", "123456");
//        Statement statement1 = connection1.createStatement();
//        ResultSet resultSet1 = statement1.executeQuery("select * from ticket_order");
//        while (resultSet1.next()){
//            TicketOrder ticketOrder = new TicketOrder();
//            ticketOrder.setUserName(resultSet1.getString("order_number"));
//            ticketOrder.setUserName(resultSet1.getString("user_name"));
//            System.out.println(ticketOrder);
//        }


//        原生方法访问myqsl
//        加载mysql驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        创建数据库连接
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ticket", "root", "123456");

//        创建执行mysql语句的对象
//        Statement statement = connection.createStatement();

//        执行mysql语句
//      ResultSet resultSet = statement.executeQuery("select * from ticket_order");

//        处理结果
//        while (resultSet.next()){
//            System.out.println(resultSet.getString("order_number")
//            + " " + resultSet.getString("user_name"));

//            TicketOrder ticketOrder = new TicketOrder();
//            ticketOrder.setOrderNumber(resultSet.getString("order_number"));
//            ticketOrder.setUserName(resultSet.getString("user_name"));
//            System.out.println(ticketOrder);


//        }


    }
    @Autowired
    TicketOrderService ticketOrderService;

    @Test
    void testService(){
        List<TicketOrder> List = ticketOrderService.list();
        for (TicketOrder list:List){
            System.out.println(list);
        }
    }


}
