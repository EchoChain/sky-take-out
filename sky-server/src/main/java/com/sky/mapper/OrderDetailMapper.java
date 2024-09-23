package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/23 15:59
 * @comment
 */
@Mapper
public interface OrderDetailMapper {

    void insertBatch(ArrayList<OrderDetail> details);
}
