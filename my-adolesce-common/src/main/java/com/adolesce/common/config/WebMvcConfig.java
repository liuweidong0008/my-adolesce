package com.adolesce.common.config;

import com.adolesce.common.intercepter.GlobalCacheInterceptor;
import com.adolesce.common.intercepter.TokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer /*extends WebMvcConfigurationSupport*/ {
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private GlobalCacheInterceptor globalCacheInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加全局缓存拦截器
        /*registry.addInterceptor(globalCacheInterceptor).addPathPatterns("/**");*/

        //添加token拦截器
       /* registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(new String[]{"/login/sendCode","/login/login","/heiMaStudentMsg/**","/excel/**"});*/
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * 扩展mvc框架的消息转换器
     */
    /*@Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中，优先使用我们自定义的消息转换器
        converters.add(0,messageConverter);
    }*/

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");

        //1、映射swagger自动生成静态资源
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        //registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        //默认WebMvcAutoConfiguration就会映射【static、templates等目录】，所以放在这些目录的资源无需任何映射就能被找到
        //但是只有在没有WebMvcConfigurationSupport的前提下才会加载WebMvcAutoConfiguration【@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})】
        //所以一旦自定义了WebMvcConfigurationSupport，static就会失效，就要重写其中的addResourceHandlers方法

        //以下是把backend和front两个静态资源文件夹直接放在resources中的映射配置
        //registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        //registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

        //以下是把所有静态资源放在resources下static目录的映射配置
        //registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        //以下是把所有静态资源放在resources下static的backend目录中的映射配置
        //registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend/");
    }
}