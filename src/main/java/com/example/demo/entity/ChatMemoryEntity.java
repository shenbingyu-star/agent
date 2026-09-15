package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-22
 * @Description: ai聊天角色
 * @Version: 1.0
 */
@Data //lombok提供getter和setter方法
@TableName("chat_memory")//数据库表名
public class ChatMemoryEntity {

 @TableId(type = IdType.AUTO)
 private Long id;

 private String memoryId;

 private String role;

 private String message;

 @Version
 private Integer version;

 @TableLogic
 private Integer deleted;

 private LocalDateTime createTime;
}
