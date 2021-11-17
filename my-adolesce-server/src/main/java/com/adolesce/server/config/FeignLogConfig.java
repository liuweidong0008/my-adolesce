package com.adolesce.server.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLogConfig {
    /**
     * Feign请求链路日志打印(用于调试)整合步骤：
     * 1、增加配置：调整日志级别为debug，Feign日志级别为debug
     * 2、编写FeignLogConfig配置类，指定打印的内容
     * 3、在接口调用类上OrderService上打上注解，指定使用该配置类
     *
     * @FeignClient(value = "EUREKA-PROVIDER",configuration = FeignLogConfig.class)
     * <p>
     * NONE,不记录
     * BASIC,记录基本的请求行，响应状态码数据
     * HEADERS,记录基本的请求行，响应状态码数据，记录响应头信息
     * FULL;记录完成的请求 响应数据
     */
    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }
}
