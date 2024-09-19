package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
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
    @Select("select * from setmeal_dish where dish_id = #{dishId}")
    List<SetmealDish> getByDishId(Long dishId);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    // 没有基础字段需要自动填充
    void insertBatch(List<SetmealDish> list);

    void deleteBatch(List<Long> setmealIds);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
