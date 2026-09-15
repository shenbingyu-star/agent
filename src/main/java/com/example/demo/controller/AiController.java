package com.example.demo.controller;

import com.example.demo.config.AiConfig;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-15
 * @Description: AI模型
 * @Version: 1.0
 */
@RestController//相当于controller+responseBody 这个控制器中的所有方法都是返回字符串的
@RequestMapping("/ai")
public class AiController {
    @Autowired
//  让spring框架注入我们想要的对象
    ChatLanguageModel chatLanguageModel;

    //    编写一个方法接受和处理用户聊天请求
    @RequestMapping("/chat")
    public String test(@RequestParam(defaultValue = "你是谁") String message) {
        String chat = chatLanguageModel.chat(message);
        return chat;
    }

    @Autowired
    AiConfig.Assistant assistant;

    @RequestMapping("/memory_chat")
    public String chat(@RequestParam(defaultValue = "你是谁") String message) {
        String response = assistant.chat(message);
        return response;
    }

    @RequestMapping(value = "/memory_stream_chat", produces = "text/stream;charset=utf-8")
    public Flux<String> stringFlux(
            @RequestParam(defaultValue = "你是谁")
            String message) {
        // 调用助手服务的流式接口，获取TokenStream对象
        TokenStream stream = assistant.stream(message, LocalDateTime.now().toString());
        // 创建Flux流式响应
        return Flux.create(sink -> {
            // 设置部分响应回调：每次收到部分响应时通过sink发送数据
            stream.onPartialResponse(s -> sink.next(s))
                    // 设置完成回调：当收到完成信号时关闭流
                    .onCompleteResponse(c -> sink.complete())
                    // 设置错误回调：发生错误时传递错误信号
                    // 它的作用是将 sink 对象的 error 方法作为函数式接口的实现传递进去。
                    .onError(error -> sink.error(error))
                    // 启动流处理
                    .start();

        });


    }

    @Autowired
    AiConfig.AssistantUnique assistantUnique;


    @RequestMapping(value = "/id_chat", produces = "text/stream;charset=utf-8")
    public Flux<String> stringFlux(
            @RequestParam(defaultValue = "你是谁")
            String message, int userId) {
        // 调用助手服务的流式接口，获取TokenStream对象
        TokenStream stream = assistantUnique.stream(message,userId);
        // 创建Flux流式响应
        return Flux.create(sink -> {
            // 设置部分响应回调：每次收到部分响应时通过sink发送数据
            stream.onPartialResponse(s -> sink.next(s))
                    // 设置完成回调：当收到完成信号时关闭流
                    .onCompleteResponse(c -> sink.complete())
                    // 设置错误回调：发生错误时传递错误信号
                    // 它的作用是将 sink 对象的 error 方法作为函数式接口的实现传递进去。
                    .onError(error -> sink.error(error))
                    // 启动流处理
                    .start();

        });
    }


}










