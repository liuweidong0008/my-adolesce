package com.adolesce.autoconfig.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    private String accessKey;
    private String secret;
    private String bucketName;
    private String url; //域名
    private String endpoint;

    @Bean
    public OSS ossClient(){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secret);
        return ossClient;
    }
}