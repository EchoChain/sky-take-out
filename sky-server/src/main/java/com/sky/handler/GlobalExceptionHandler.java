package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SQLException.class)
    public Result<String> sqlExceptionHandler(SQLException ex){
        log.error("SQL Exception: {}", ex.getMessage());

        if (ex instanceof SQLIntegrityConstraintViolationException) {
            // msg:Duplicate entry 'zhangsan' for key 'employee.idx_username'
            String message = ex.getMessage();
            String[] split = message.split(" ");
            String user = split[2];
            return Result.error(user + MessageConstant.ALREADY_EXISTS);
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    @ExceptionHandler(DeletionNotAllowedException.class)
    public Result<String> deletionNotAllowedExceptionHandler(DeletionNotAllowedException ex) {
        log.error("DeletionNotAllowed Exception: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(SetmealEnableFailedException.class)
    public Result<String> SetmealEnableFailedExceptionHandler(SetmealEnableFailedException ex) {
        log.error("SetmealEnableFailed Exception: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(OrderBusinessException.class)
    public Result<String> orderBusinessExceptionHandler(OrderBusinessException ex) {
        log.error("OrderBusinessException Exception: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
