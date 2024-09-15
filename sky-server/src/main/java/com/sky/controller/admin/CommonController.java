package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.sky.result.Result;
import com.sky.utils.AliOSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/15 18:00
 * @comment
 */
@Slf4j
@Api(tags = "通用接口")
@RestController
@RequestMapping("admin/common")
public class CommonController {
    @Autowired
    private AliOSSUtil aliOSSUtil;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        String url = aliOSSUtil.upload(file);
        return Result.success(url);
    }
}
