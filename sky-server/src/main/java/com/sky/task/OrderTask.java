package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")//every minute
    public void processTimeOutOrder(){
        log.info("定时处理未支付超时订单:{}", LocalDateTime.now());
        //select * from orders where status = unpaid and createTime < 15min
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(
                                Orders.PENDING_PAYMENT,
                                LocalDateTime.now().plusMinutes(-15));
        if(orders!=null && !orders.isEmpty()){
            orders.forEach(o->{
                o.setStatus(Orders.CANCELLED);
                o.setCancelReason("订单超时，自动取消");
                o.setCancelTime(LocalDateTime.now());
                orderMapper.update(o);
            });
        }
    }
    @Scheduled(cron = "0 0 1 * * ?")//1am everyday
    public void processDeliveryOrder(){
        log.info("定时处理派送中订单:{}", LocalDateTime.now());
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(
                Orders.DELIVERY_IN_PROGRESS,
                LocalDateTime.now().plusMinutes(-60));
        if(orders!=null && !orders.isEmpty()) {
            orders.forEach(o -> {
                o.setStatus(Orders.COMPLETED);
                orderMapper.update(o);
            });
        }
    }
}
