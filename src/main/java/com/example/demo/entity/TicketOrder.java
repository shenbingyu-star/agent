package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import net.sf.jsqlparser.schema.Table;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: 小伙子
 * @CreateTime: 2025-10-21
 * @Description: 连接数据库
 * @Version: 1.0
 */
@Data
@TableName("ticket_order")//类名和表名不一致时使用
public class TicketOrder {
    /**
     * 基本类型（long 或 int），而不是包装类型（Long 或 Integer）
     * 基本类型long 或 int
     * 无法表示 null	数据库主键在未插入前是 null，但 long 默认值是 0，会误导框架
     * MyBatis-Plus 警告	MyBatis-Plus 明确建议主键用包装类，否则可能踩坑
     * 逻辑错误风险	如果你用 0 判断“未保存”，会和真正 ID 为 0 的记录混淆
     * // ❌ 不推荐
     * private long id;
     *
     * // ✅ 推荐
     * private Long id;
     *
     *包装类型Long 或 Integer
     *接收 null（插入前数据库未生成值时）
     * 与 MyBatis-Plus 的主键自动填充策略（IdType.AUTO、ASSIGN_ID 等）完美配合
     * 避免各种 “0 还是 null” 的逻辑歧义
     * 列名和属性名不一致 ≠ 下划线驼峰差异时，必须加 @TableField("真实列名")，否则报错！
     *
     */
    @TableId(type = IdType.AUTO)//生成自增主键
    private Long id;//订单的唯一标识符，主键，自动生成
    private String orderNumber;//高铁班次
    private String userName; //   下单用户的用户名或姓名
    private String departure;//   出发城市，例如“北京”
    private String destination;//  目的城市，例如“上海”
    private int price;
    private LocalDate travelDate;//   乘车日期，例如：2025-07-20
    // 数据库`travel_date` DATE NOT NULL,
    private Boolean refunded; //表示该订单是否已经退票。false 代表未退票，true 代表已退票

    @Version
    //    @Version = 乐观锁令牌，更新时自动带上版本号，防止并发写丢失；
//    零配置、一注解、先查后改，是并发安全的最廉价方案。
//    更新时自动带上“版本号”作为条件，防止并发更新互相覆盖（ Lost Update ）；
//    更新成功版本号 +1，失败则抛出 OptimisticLockerException，由业务决定重试或提示用户。
    //防止并发 10个线程同时访问只有 1 个线程返回 true，
    // 其余 9 条都会抛 OptimisticLockerException。
    private Integer version;

    @TableLogic

//    @TableLogic 是 MyBatis-Plus 提供的**「逻辑删除」**注解，一句话解释：
//    标记后，执行 deleteById 时不会真的执行 DELETE 语句，
//    而是自动把 deleted 字段更新为已删除值；
//    后续所有查询也会自动加上 deleted = 0 的条件，实现“假删”。
    private Integer deleted;

    private LocalDateTime createTime;//   创建时间，例如：2025-10-21 09:23:47
// 数据库`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP默认当前时间  时间戳










}
