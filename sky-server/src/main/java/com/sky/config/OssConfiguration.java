package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOSSUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/15 17:47
 * @comment
 */
@Configuration
@EnableConfigurationProperties({AliOssProperties.class})
public class OssConfiguration {
    @Bean
    public AliOSSUtil aliOSSUtil(AliOssProperties aliOssProperties) {
        return new AliOSSUtil(aliOssProperties);
    }
}
