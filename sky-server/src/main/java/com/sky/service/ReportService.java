package com.sky.service;

import com.sky.vo.TurnoverReportVO;

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
}
