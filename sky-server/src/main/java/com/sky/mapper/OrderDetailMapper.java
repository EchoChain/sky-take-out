package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/23 15:59
 * @comment
 */
@Mapper
public interface OrderDetailMapper {

    void insertBatch(ArrayList<OrderDetail> details);

    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrdersId(Long orderId);

    List<GoodsSalesDTO> countTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
