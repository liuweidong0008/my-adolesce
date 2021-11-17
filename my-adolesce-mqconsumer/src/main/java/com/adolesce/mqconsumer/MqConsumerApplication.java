package com.adolesce.mqconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 启动类
 */
//排除mongoDB数据源，用不到(目前不排除也行，因为本地安装了mongodb)
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MqConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqConsumerApplication.class, args);
    }
}