package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    // 没有基础字段需要自动填充
    void insertBatch(List<SetmealDish> list);
}
