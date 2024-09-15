package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/15 19:11
 * @comment
 */
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.insert(dish);
        Long dishId = dish.getId();  // 由于 mapper 设置了主键返回 插入后 dish 能获取到 id

        if (flavors != null && !flavors.isEmpty())
            flavors.forEach(flavor -> flavor.setDishId(dishId));    // 用 for 循环将每个口味的 dishId 都进行设置

        dishFlavorMapper.insertBatch(flavors);
    }
}
