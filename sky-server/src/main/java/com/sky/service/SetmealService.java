package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/19 16:22
 * @comment
 */
public interface SetmealService {
    void save(SetmealDTO setmealDTO);
    PageResult<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);
    void deleteWithDish(List<Long> ids);
}
