package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-14
 * @Description: 测试用例
 * @Version: 1.0
 */
//定义一个控制器 控制器：用来接受和处理http请求
@Controller//表示这个类是一个控制器
@RequestMapping("/fan")
public class HelloController {
    //Spring MVC 中控制器是基于方法来拦截和处理请求的
    //http:localhost:8080/fan/hello
    @RequestMapping("/hello")//定义这个方法访问的URL
    @ResponseBody//表示这个方法只会返回一个字符串作为响应结果
    public String hello(@RequestParam String message) {
        //接受到请求后可以在当前方法中处理
        System.out.println("当前hello()接口被访问了。。。。。");
        System.out.println("message:" + message);
        return "hello success build.....";
    }

    @ResponseBody
    @RequestMapping("/login")
    //页面javascript => JSON字符串（{username："admin",password:"admin"}）=>java对象
    public String login(@RequestBody User user) {//@RequestBody表示获取请求体中的数据
        System.out.println("账号=" + user.getUsername());
        System.out.println("密码=" + user.getPassword());
        System.out.println("-------------------------------------");
        //TODO 获取账号密码后可以去数据库中进行校验
        return "success login!!!";
    }
}
