package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/20 19:47
 * @comment
 */

@RestController
@RequestMapping("/user/user")
@Api(tags = "用户端用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("用户微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) throws IOException {
        User user = userService.login(userLoginDTO);
        HashMap<String, Object> claim = new HashMap<>();
        claim.put(JwtClaimsConstant.USER_ID, user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claim);
        UserLoginVO userLoginVO = UserLoginVO
                .builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();
        return Result.success(userLoginVO);
    }
}
