package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.TicketOrder;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-17
 * @Description: 特定业务
 * @Version: 1.0
 */

@Service
public class TooolsService {
    @Tool("云浮有多少个叫什么的人")
    public Integer nameCount(@P("姓名")String name){
        //TODO 接入查询系统的代码
        //去业务逻辑，只有AI识别到与这个问题相关的时候 就会调用这个函数 并且重新组织语言
        if (name.equals("小红")){
            return 1000;}
        else if(name.equals("小何")){
            return 300;}
        return 3;
    }
//退票的业务逻辑代码
    @Tool("处理退票请求 需要客户提供列车的班号、客户姓名、客户身份证")
    public String  ticket(@P("列车的班号")String trainNo,@P("客户姓名")String name,@P("客户身份证")String IDCard){
        System.out.println("列车的班号" + trainNo);
        System.out.println("客户姓名" + name);
        System.out.println("客户身份证" + IDCard);
        //TODO编写退票的业务逻辑代码
//模拟退票流程
        double v = Math.random() * 10;
        if(v > 5){
            return "退票成功";}
        else{ return "退票失败";}
    }
//    {
//        // 1. 查询订单
//        TicketOrder order = ticketOrderMapper.selectOne(
//                new LambdaQueryWrapper<TicketOrder>()
//                        .eq(TicketOrder::getOrderNumber, trainNo)
//                        .eq(TicketOrder::getUserName, name)
//        );
//
//        if (order == null) {
//            return "未找到订单，请提示用户核对班号和姓名";
//        }
//
//        // 2. 校验身份证
//        if (!order.getIdCard().equals(IDCard)) {
//            return "身份证信息不匹配，请提示用户重新提供";
//        }
//
//        // 3. 校验是否已退票
//        if (order.getRefunded()) {
//            return "该订单已退票，请提示用户无需重复操作";
//        }
//
//        // 4. 校验发车时间 —— 这里是关键
//        LocalDateTime departureTime = order.getTravelDate().atTime(order.getDepartureTime());
//        long minutesUntilDeparture = ChronoUnit.MINUTES.between(LocalDateTime.now(), departureTime);
//
//        if (minutesUntilDeparture < 30) {
//            return "该订单距发车时间已不足30分钟，根据规定无法办理退票。请向用户说明情况并表示歉意。";
//        }
//
//        // 5. 计算退票手续费（根据距发车时间分档）
//        String feeInfo = calculateRefundFee(minutesUntilDeparture, order.getPrice());
//
//        // 6. 执行退票
//        order.setRefunded(true);
//        ticketOrderMapper.updateById(order);
//
//        return "退票成功！" + feeInfo;
//    }
}
