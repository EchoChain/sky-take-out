package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/20 15:07
 * @comment
 */
@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    public Result<Integer> setStatus(@PathVariable Integer status) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY, status);
        log.info("设置店铺状态为: {}", status == 1? "营业中" : "打烊中");
        return Result.success(status);
    }

    @GetMapping("/status")
    @ApiOperation("查询店铺的营业状态")
    public Result<Integer> getStatus() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(KEY);
        log.info("商家查询店铺状态为: {}", status == 1? "营业中" : "打烊中");
        return Result.success(status);
    }
}
