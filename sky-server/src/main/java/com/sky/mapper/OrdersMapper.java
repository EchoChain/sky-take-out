package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/23 15:43
 * @comment
 */
@Mapper
public interface OrdersMapper {

    void insert(Orders orders);
}
