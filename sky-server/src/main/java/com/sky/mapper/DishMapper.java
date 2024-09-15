package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Options(useGeneratedKeys = true, keyProperty = "id")  // 主键返回
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
}
