package com.adolesce.nacos.feign.config;

import com.adolesce.nacos.feign.clients.fallback.GoodsFeignClientFactory;
import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public GoodsFeignClientFactory userClientFallbackFactory(){
        return new GoodsFeignClientFactory();
    }
}
