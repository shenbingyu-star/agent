package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.TicketOrder;
import com.example.demo.mapper.TicketOrderMapper;
import com.example.demo.service.TicketOrderService;
import com.example.demo.service.TooolsService;
import org.springframework.stereotype.Service;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-21
 * @Description: 业务逻辑代码接口的实现类
 * @Version: 1.0
 */
//一定要使用注解声明@Service 表示他是业务类代码
@Service
public class TicketOrderServiceImpl extends
        ServiceImpl<TicketOrderMapper,TicketOrder> implements TicketOrderService {


    @Override
    public String testOrder(String userNumber) {
        return null;
    }
}
