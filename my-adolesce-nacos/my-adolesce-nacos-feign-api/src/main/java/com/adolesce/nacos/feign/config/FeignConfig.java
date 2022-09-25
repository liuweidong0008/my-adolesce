package com.adolesce.nacos.feign.config;

import com.adolesce.nacos.feign.clients.fallback.GoodsFeignClientFactory;
import com.adolesce.nacos.feign.interceptor.MyFeignInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

//@EnableFeignClients(basePackages = "com.adolesce.nacos.feign.clients")
//@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new MyFeignInterceptor();
    }
    @Bean
    public Logger.Level level(){
        return Logger.Level.BASIC;
    }

    @Bean
    public GoodsFeignClientFactory goodsFeignClientFactory(){
        return new GoodsFeignClientFactory();
    }
}
