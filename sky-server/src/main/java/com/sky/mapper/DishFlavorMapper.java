package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/15 19:26
 * @comment
 */
@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> dishFlavors);

    void deleteBatch(List<Long> dishIds);
}
