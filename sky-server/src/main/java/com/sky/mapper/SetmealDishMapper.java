package com.sky.mapper;

import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/19 14:21
 * @comment
 */
@Mapper
public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where id = #{dishId}")
    Setmeal getByDishId(Long dishId);
}
