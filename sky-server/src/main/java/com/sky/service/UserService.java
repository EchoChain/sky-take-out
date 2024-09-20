package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

import java.io.IOException;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/20 19:50
 * @comment
 */
public interface UserService {
    public User login(UserLoginDTO userLoginDTO) throws IOException;
}
