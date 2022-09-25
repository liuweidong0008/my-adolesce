package com.adolesce.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ConfigurationProperties注解读取application.yml或application.propertis
 * 1、加上@Configuration 或 @Component注解 或者在使用类如Controller上加上 @EnableConfigurationProperties({DemoProperties.class})注解，
 *    目的是：将Properties配置类加入到spring容器中
 * 2、配置类加上@ConfigurationProperties(prefix="")注解读取application.yml或application.propertis
 * 3、另外可结合@PropertySource读取自定义配置文件(只要能被spring容器扫描到，放在哪里都可以)
 * 4、如果各配置文件中有相同属性配置，属性优先级为
 *
 *    config:application-dev.propertis > config:application-dev.yml > application-dev.propertis >  application-dev.yml >
 *    config:application.propertis > config:application.yml > config:application.yaml >
 *    application.propertis        > application.yml        > application.yaml >       自定义配置文件
 *
 *
 *    属性优先级结论：
 *      0）、前提：位置优先级
 *          -file:./config/
 *          -file:./
 *          -classpath:/config/
 *          -classpath:/
 *      1）、带profile的文件属性优先级最高，而后才是不带profile的文件属性，同名的profile文件，属性优先级遵循位置优先级
 *      2）、加载不带profile的文件时，位置优先级越高的，属性优先级越高，同一个位置时，遵循以下优先级：
 *           propertis > yml > yaml
 *
 * 5、@PropertySource的value属性指定加载了多个自定义配置文件，加载顺序为从左到右顺序加载，后加载的会覆盖先加载的属性值。
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "adolesce")//(prefix = "demo")
//@PropertySource({"classpath:demo.properties"})
public class DemoProperties {
    private String one;
    private String two;
    private String three;
    private List<String> excludUrls;
}