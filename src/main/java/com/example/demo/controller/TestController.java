package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-13
 * @Description: 测试用例
 * @Version: 1.0
 */

@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot 3! 当前时间: " + java.time.LocalDateTime.now();
    }

    @GetMapping("/json")
    public Object json() {
        return new java.util.HashMap<String, Object>() {{
            put("name", "Spring Boot");
            put("version", "3.5.6");
            put("timestamp", java.time.Instant.now());
        }};
    }
}
