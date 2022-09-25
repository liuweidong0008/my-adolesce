package com.adolesce.autoconfig;


import com.adolesce.autoconfig.config.*;
import com.adolesce.autoconfig.template.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 自动装配（利用SpringBoot的自动配置特性）
 *  步骤：
 *      1、定义一个普通的java类SmsTemplate，内部封装了关于短信发送的功能，里面定义一个接收配置类YiMeiSMSProperties的构造方法
 *
 *      2、编写自动配置类AutoConfiguration，利用@Bean定义SmsTemplate，返回SmsTemplate对象，将来交由spring容器管理，
 *         并且通过@EnableConfigurationProperties 在当前类将配置类放入spring容器，
 *         而后将配置类YiMeiSMSProperties从容器中取出然后传递进SmsTemplate
 *
 *      3、在resources/META-INFO 目录定义 spring.factories文件，利用springboot自动装配机制，对自动装配类进行加载：
 *         org.springframework.boot.autoconfigure.EnableAutoConfiguration=\com.adolesce.common.autoconfig.AutoConfiguration
 */

/**
 * 关于读取配置方面
 *  1、@EnableConfigurationProperties 这个注解和@ConfigurationProperties注解是一个完美组合，@ConfigurationProperties用于以 前缀方式读取配置文件，
 *      而@EnableConfigurationProperties 这个注解可以把被@ConfigurationProperties标注的类注入spring容器
 *  2、@Component可以将Properties类放入spring容器，但是前提是Spring必须要扫描到该properties
 *  3、如果是读取自定义的配置文件，需借助@PropertySource("classpath:sms.properties") 注解指定文件位置及文件名，且必须打上@Component类型的注解,而且要被spring扫描到，才能被装配读取到配置
 *  4、如果springboot的application文件和自定义的配置文件中有相同的配置项，优先读取spring boot的application文件中的
 *  5、关于springboot配置文件属性优先级可以查看PropertiesDemoController
 */
@EnableConfigurationProperties({
        YiMeiSMSProperties.class,
        HuyiSMSProperties.class,
        OssProperties.class,
        AliyunGreenProperties.class,
        AliyunVisionProperties.class
})
@Import({MinioTemplate.class,MailTemplate.class})
public class AdolesceAutoConfiguration {
    @Bean
    public SmsTemplate smsTemplate(YiMeiSMSProperties yiMeiSMSProperties,
                                   HuyiSMSProperties huyiSMSProperties) {
        return new SmsTemplate(yiMeiSMSProperties, huyiSMSProperties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties properties) {
        return new OssTemplate(properties);
    }

    /**
     * 检测配置文件中，是否具有tanhua.green开头的配置
     *      同时，其中的enable属性 = true
     */
    @Bean
    @ConditionalOnProperty(prefix = "aliyun.content-security.green",value = "enable", havingValue = "true")
    //@ConditionalOnProperty(name = "aliyun.content-security.green.enable", havingValue = "true")
    public AliyunGreenTemplate aliyunGreenTemplate(AliyunGreenProperties properties) {
        return new AliyunGreenTemplate(properties);
    }

    /**
     * 检测配置文件中，是否具有tanhua.vision开头的配置
     *      同时，其中的enable属性 = true
     */
    @Bean
    @ConditionalOnProperty(prefix = "aliyun.content-security.vision",value = "enable", havingValue = "true")
    public AliyunVisionTemplate aliyunVisionTemplate(AliyunVisionProperties properties) {
        return new AliyunVisionTemplate(properties);
    }
}