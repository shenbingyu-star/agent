package com.example.demo.entity;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-14
 * @Description: 用户实体
 * @Version: 1.0
 */

//封装属性到User实体
//将账号密码封装到user用户类中 可以进行统一管理，也可以统一接受数据
public class User {
    //私有成员变量 账号 密码
    private String username;
    private String password;

    //java中私有的成员属性 无法在外部直接访问 我们可以通过getter和setter方法来进行访问
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
