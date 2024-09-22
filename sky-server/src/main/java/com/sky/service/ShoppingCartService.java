package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/22 15:44
 * @comment
 */
public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
    List<ShoppingCart> list();
}
