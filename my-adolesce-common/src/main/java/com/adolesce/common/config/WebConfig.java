package com.adolesce.common.config;

import com.adolesce.common.handler.JacksonObjectMapper;
import com.adolesce.common.intercepter.GlobalCacheInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;

@Slf4j
@Configuration
public class WebConfig extends WebMvcConfigurationSupport /*implements WebMvcConfigurer*/ {

    @Autowired(required = false)
    private GlobalCacheInterceptor globalCacheInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(globalCacheInterceptor).addPathPatterns("/**");
    }

    @Override
    protected void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中，优先使用我们自定义的消息转换器
        converters.add(0,messageConverter);
    }
}