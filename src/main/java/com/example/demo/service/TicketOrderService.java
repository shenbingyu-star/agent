package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.TicketOrder;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-21
 * @Description: 加入业务逻辑代码接口
 * @Version: 1.0
 */
//Service层一般会写成先定义接口 然后再写接口的实现类
//业务逻辑代码 看你比较长 不好维护 我们通常采用就在接口中定义一个类中有什么方法
//这里继承了IService目的是为了直接调用mybatis plus提供的基础增删改查代码
public interface TicketOrderService extends IService<TicketOrder> {

    String testOrder(String userNumber);


}
