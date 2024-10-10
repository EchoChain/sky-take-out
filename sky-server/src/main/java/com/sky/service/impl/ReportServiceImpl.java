package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
}
