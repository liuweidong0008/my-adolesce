package com.adolesce.cloud.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 启动类
 */
@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class, MongoAutoConfiguration.class})
@MapperScan("com.adolesce.cloud.db.mapper")
/*@ComponentScan(basePackages = "com.adolesce")*/
@EntityScan("com.adolesce.cloud.dubbo.domain")
public class CloudDubboDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudDubboDBApplication.class, args);
    }
}