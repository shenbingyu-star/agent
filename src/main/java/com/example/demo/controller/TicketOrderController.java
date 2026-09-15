package com.example.demo.controller;

import com.example.demo.entity.TicketOrder;
import com.example.demo.service.TicketOrderService;
import com.example.demo.service.TooolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.POST;

import java.util.List;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-21
 * @Description: 编写控制器代码
 * @Version: 1.0
 *
 *
 *
 * @RequestMapping / @GetMapping / @PostMapping / @PutMapping / @DeleteMapping
 * @PathVariable → 取 /order/{id} 里的 id
 * @RequestParam → 取 ?page=1&size=10 里的参数
 * @RequestBody → 把前端 JSON 反序列化成 JavaBean（必须加 @Valid 做参数校验）
 * @ResponseStatus → 自定义返回的 HTTP 状态码
 * @ExceptionHandler / @RestControllerAdvice → 统一异常处理（返回 JSON 错误报文）
 */

@RestController  // 1. 交給 Spring 扫描并托管；2. 默认全返回 JSON
//@RequestMapping("/orders")
public class TicketOrderController {
    @Autowired
    private TicketOrderService ticketOrderService;

    //查询
    @GetMapping("/orders")
    public List<TicketOrder> getAllOrders(){
        return ticketOrderService.list();
    }

    //增加
    @PostMapping("/orders")
    public boolean addOrders(@RequestBody TicketOrder ticketOrder){
        return ticketOrderService.save(ticketOrder);
    }

    //修改
    @PutMapping("/orders")
    public boolean updateOrders(@RequestBody TicketOrder ticketOrder){
        return ticketOrderService.updateById(ticketOrder);
    }
//    删除
    @DeleteMapping("/orders/{id}")
    public boolean deleteOrders(@PathVariable long id){
        return ticketOrderService.removeById(id);
    }

}
