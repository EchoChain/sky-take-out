package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/26 20:06
 * @comment
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrdersMapper ordersMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("处理支付超时订单：{}", new Date());

        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = ordersMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                ordersMapper.update(order);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理派送中订单：{}", new Date());

        // select * from orders where status = 4 and order_time < 当前时间-1小时f
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> orders = ordersMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        if(orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(LocalDateTime.now());
                ordersMapper.update(order);
            }
        }
    }
}
