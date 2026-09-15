package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-21
 * @Description: 加入mapper数据库访问层代码
 * @Version: 1.0
 */
//extends BaseMapper<TicketOrder> 去继承mybatis plus 提供的基础增删改查代码
@Mapper//表示这个接口是数据库访问代码层
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {



}
