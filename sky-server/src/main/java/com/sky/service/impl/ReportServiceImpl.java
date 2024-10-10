package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/28 13:01
 * @comment
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>(); //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        dateList.add(begin);
        LocalDate tmp = begin;
        while (!tmp.equals(end)) {
            tmp = tmp.plusDays(1L);
            dateList.add(tmp);
        }

        ArrayList<Double> turnoverList = new ArrayList<>(); //营业额，以逗号分隔，例如：406.0,1520.0,75.0
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", beginTime);
            map.put("end", endTime);
            Double turnover =  ordersMapper.sumByMap(map);
            turnover = turnover == null ? 0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

    }

    @Override
    public UserReportVO getUser(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>(); //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        dateList.add(begin);
        LocalDate tmp = begin;
        while (!tmp.equals(end)) {
            tmp = tmp.plusDays(1L);
            dateList.add(tmp);
        }

        ArrayList<Integer> totalUserList = new ArrayList<>();  //用户总量，以逗号分隔，例如：200,210,220
        ArrayList<Integer> newUserList = new ArrayList<>();    //新增用户，以逗号分隔，例如：20,21,10

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 新增用户数量 select count(id) from user where create_time > ? and create_time < ?
            Integer newUser = userMapper.countByBeginAndEnd(beginTime, endTime);
            // 总用户数量 select count(id) from user where  create_time < ?
            Integer totalUser = userMapper.countByBeginAndEnd(null, endTime);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrders(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>(); //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        dateList.add(begin);
        LocalDate tmp = begin;
        while (!tmp.equals(end)) {
            tmp = tmp.plusDays(1L);
            dateList.add(tmp);
        }

        ArrayList<Integer> orderCountList = new ArrayList<>();  //每日订单数，以逗号分隔，例如：260,210,215
        ArrayList<Integer> validOrderCountList = new ArrayList<>();  //每日有效订单数，以逗号分隔，例如：20,21,10
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = ordersMapper.countByTimeAndStatus(beginTime, endTime, null);
            Integer validOrderCount = ordersMapper.countByTimeAndStatus(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        Integer totalOrderCount = ordersMapper.countByTimeAndStatus(null, null, null);
        Integer validOrderCount = ordersMapper.countByTimeAndStatus(null, null, Orders.COMPLETED);
        Double orderCompletionRate = totalOrderCount == 0 ? 0 : validOrderCount * 1.0 / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> dtoList =  orderDetailMapper.countTop10(beginTime, endTime);
        String nameList = StringUtils.join(
                dtoList.stream().map(GoodsSalesDTO::getName).toList(),
                ",");
        String numberList = StringUtils.join(
                dtoList.stream().map(GoodsSalesDTO::getNumber).toList(),
                ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
