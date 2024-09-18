package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/15 19:11
 * @comment
 */
public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);
    PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO);
}
