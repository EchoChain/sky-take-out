package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/24 20:32
 * @comment
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单条件查询")
    public Result<PageResult<OrderVO>> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult<OrderVO> page = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(page);
    }

    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO vo = orderService.statistics();
        return Result.success(vo);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getDetails(@PathVariable Long id) {
        OrderVO vo = orderService.getDetails(id);
        return Result.success(vo);
    }

    @PutMapping("/confirm")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirmById(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.rejectionById(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancelById(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result<String> delivery(@PathVariable Long id) {
        orderService.deliveryById(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id) {
        orderService.completeById(id);
        return Result.success();
    }
}
