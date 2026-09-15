package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.ChatMemoryEntity;
import com.example.demo.mapper.ChatMemoryMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-22
 * @Description: // 自定义 ChatMemoryStore 实现
 * @Version: 1.0
 */
@Configuration
public class MySqlChatMemoryStore implements ChatMemoryStore {
    @Autowired
    ChatMemoryMapper chatMemoryMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryIdObject) {
        //判断是否存在聊天者id
        if (memoryIdObject == null) {
            try {
                throw new IllegalAccessException("memoryId不能为空");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
//        判断有没有历史聊天记录
        String memoryId = memoryIdObject.toString();
        QueryWrapper<ChatMemoryEntity> wrapper = new QueryWrapper<>();
        //判断是否有这个聊天者id 有就按创建时间升序排序
        wrapper.eq("memory_id", memoryId).orderByAsc("create_time");
        //调出聊天记录
        List<ChatMemoryEntity> list = chatMemoryMapper.selectList(wrapper);
        //解析聊天记录
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        for (ChatMemoryEntity chatMemoryEntity : list) {
           //获取角色
            String role = chatMemoryEntity.getRole().toUpperCase();
//            获取内容
            String message = chatMemoryEntity.getMessage();
//            将聊天记录分类
            switch (role){
                case "USER" ->chatMessages.add(UserMessage.from(message));
                case "AI" ->chatMessages.add(AiMessage.from(message));
                case "SYSTEM" ->chatMessages.add(SystemMessage.from(message));
            }

        }
        if (chatMessages.isEmpty()) {
            chatMessages.add(SystemMessage.from("你好！我是DeepSeek智能助手，有什么可以帮你的吗？"));

        }


        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryIdObject, List<ChatMessage> chatMessages) {
        //判断聊天信息是否为空
        if (memoryIdObject == null || chatMessages == null || chatMessages.isEmpty()) {
            return;
        }
        //不为空 就新建实体类 把聊天信息放到数据库中
        for (ChatMessage chatMessage : chatMessages) {
            ChatMemoryEntity chatMemoryEntity = new ChatMemoryEntity();
            chatMemoryEntity.setMemoryId(memoryIdObject.toString());
            chatMemoryEntity.setMessage(chatMessage.toString());
            chatMemoryEntity.setRole(chatMessage.type().name());
            //更新到数据库中
            chatMemoryMapper.insert(chatMemoryEntity);
        }

    }

    @Override
    public void deleteMessages(Object memoryIdObject) {
        if (memoryIdObject == null) {
            return;
        }
        QueryWrapper<ChatMemoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("memory_id", memoryIdObject);
        chatMemoryMapper.delete(wrapper);

    }
}
