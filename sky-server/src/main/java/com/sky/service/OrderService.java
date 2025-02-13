package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/23 15:20
 * @comment
 */
public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    // 订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    // 支付成功 修改订单状态
    void paySuccess(String outTradeNo);

    PageResult<OrderVO> pageQuery(Integer page, Integer pageSize, Integer status);

    OrderVO getDetails(Long id);

    void cancelByOrderId(Long id) throws Exception;

    void repetition(Long id);

    PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirmById(OrdersConfirmDTO ordersConfirmDTO);

    void rejectionById(OrdersRejectionDTO ordersRejectionDTO);

    void cancelById(OrdersCancelDTO ordersCancelDTO);
    
    void deliveryById(Long id);

    void completeById(Long id);

    void reminder(Long id);
}
