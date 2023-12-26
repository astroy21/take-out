package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("提交订单:{}",ordersSubmitDTO);
        return Result.success(orderService.submitOrder(ordersSubmitDTO));
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> page(int page,int pageSize, Integer status){
        log.info("分页查询历史订单, 页数{},页大小{},状态{}",page,pageSize,status);
        return Result.success(orderService.pageQueryForUser(page,pageSize,status));
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("查询订单详情:{}",id);
        return Result.success(orderService.details(id));
    }

    /**
     * 用户取消订单
     * @param id
     * @return
     * @throws Exception
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) throws Exception{
        log.info("取消订单:{}",id);
        orderService.UserCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单:{}",id);
        orderService.repetition(id);
        return Result.success();
    }

}
