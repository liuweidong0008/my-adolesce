package com.adolesce.autoconfig;


import com.adolesce.autoconfig.config.HuyiSMSConfig;
import com.adolesce.autoconfig.config.YiMeiSMSConfig;
import com.adolesce.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 自动装配（利用SpringBoot的自动配置特性）
 *  步骤：
 *      1、定义一个普通的java类SmsTemplate，内部封装了关于短信发送的功能，里面定义一个接收配置类YiMeiSMSProperties的构造方法
 *
 *      2、编写自动配置类AutoConfiguration，利用@Bean定义SmsTemplate，返回SmsTemplate对象，将来交由spring容器管理，
 *         并且通过@EnableConfigurationProperties(在当前类引入配置类)将YiMeiSMSProperties传递进SmsTemplate
 *
 *      3、在resources/META-INFO 目录定义 spring.factories文件，利用springboot自动装配机制，对自动装配类进行加载：
 *         org.springframework.boot.autoconfigure.EnableAutoConfiguration=\com.adolesce.common.autoconfig.AutoConfiguration
 */

/**
 * 关于读取配置方面
 *  1、@EnableConfigurationProperties 这个注解和@ConfigurationProperties注解是一个完美组合，@ConfigurationProperties用于以 前缀方式读取配置文件，
 *      而@EnableConfigurationProperties 这个注解可以把被@ConfigurationProperties标注的类注入spring容器
 *  2、@Component可以将Properties类放入spring容器，但是前提是必须要扫描到该properties
 *  3、如果是读取自定义的配置文件，需借助@PropertySource("classpath:sms.properties") 注解指定文件位置及文件名，且必须打上@Component类型的注解进行装配才能读取到配置
 *  4、如果springboot的application文件和自定义的配置文件中有相同的配置项，优先读取spring boot的application文件中的
 */
@EnableConfigurationProperties({
        YiMeiSMSConfig.class,
        HuyiSMSConfig.class
})
public class AdolesceAutoConfiguration {
    @Bean
    public SmsTemplate smsTemplate(YiMeiSMSConfig yiMeiSMSConfig,
                                   HuyiSMSConfig huyiSMSConfig ) {
        return new SmsTemplate(yiMeiSMSConfig,huyiSMSConfig);
    }

}
