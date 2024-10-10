package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/28 13:01
 * @comment
 */
public interface ReportService {
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    UserReportVO getUser(LocalDate begin, LocalDate end);

    OrderReportVO getOrders(LocalDate begin, LocalDate end);
}
