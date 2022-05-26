package com.adolesce.common.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 *
 * 1、用于ConfigurableApplicationContext通过调用refresh函数来初始化Spring容器之前的回调函数；
 * 2、通常在web应用中，设计在初始化Spring容器之前调用。例如依赖于容器ConfigurableApplicationContext中的Enviroment来记录一些配置信息或者使一些配置文件生效；
 *   参考ContextLoader和FrameworkServlet中支持定义contextInitializerClasses作为context-param或定义init-param。
 * 3、支持Order注解，表示执行顺序，越小越早执行。
 *
 *
 * 这里有如下三种方式可以将我们自己定义的MyApplicationContextInitializer加载到Spring容器，如果有多个，用逗号隔开
 * 1、在application.yml文件指定
 *      context:
 *         initializer:
 *              classes: com.adolesce.common.init.MyApplicationContextInitializer
 *
 * 2、利用SpringBoot的SPI扩展机制，在类路径下的META-INF/spring.factories指定
 *      org.springframework.context.ApplicationContextInitializer = \
 *          com.adolesce.common.init.MyApplicationContextInitializer
 *
 * 3、改造启动类代码
 *   SpringApplication springApplication = new SpringApplication(ServerApplication.class);
 *   springApplication.addInitializers(new MyApplicationContextInitializer());
 *   springApplication.run(args);
 *
 */
@Component
public class MyApplicationContextInitializer implements ApplicationContextInitializer {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;//nohave

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.err.println("1、通过ApplicationContextInitializer初始化执行....");
    }
}
