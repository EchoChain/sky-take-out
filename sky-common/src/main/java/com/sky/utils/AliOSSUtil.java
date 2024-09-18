package com.sky.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.sky.properties.AliOssProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AliOSSUtil {

    private AliOssProperties aliOssProperties;

    public String upload(MultipartFile multipartFile){
        // 从配置类对象中获取对应的字段值
        String endpoint = aliOssProperties.getEndpoint();
        String bucketName = aliOssProperties.getBucketName();

        try {
            // 从环境变量中获取访问凭证。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            // Object完整路径 fileDir/xxxx.extra
            String filename = multipartFile.getOriginalFilename();
            String extra = filename.substring(filename.lastIndexOf('.'));
            String objectName = "fileDir/" + UUID.randomUUID().toString() + extra;
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

            // 获得源文件的输入流作为putObject的参数，将数据存放到OSSClient实例中
            InputStream inputStream = multipartFile.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);
            ossClient.shutdown();

            // 返回url
            // 例如：https://cheng-ilias.oss-cn-hangzhou.aliyuncs.com/fileDir/0748f525-9a01-4672-9ff6-4e7a37eeeb00.txt
            String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
            log.info("文件上传成功: {}", url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
