package com.sky.controller.user;

import com.sky.constant.RedisKeyConstant;
import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/20 15:04
 * @comment
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/status")
    @ApiOperation("查询店铺的营业状态")
    public Result<Integer> getStatus() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(RedisKeyConstant.SHOP_STATUS);
        log.info("用户查询店铺状态为: {}", status == StatusConstant.ENABLE? "营业中" : "打烊中");
        return Result.success(status);
    }
}
