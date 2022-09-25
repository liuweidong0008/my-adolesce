package com.adolesce.common.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/5/29 21:38
 */
@Component
public class MyBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {
    @PostConstruct  //init-method
    public void myinit() {
        System.err.println("2.4、通过@PostConstruct初始化执行....");
    }

    /**
     * BeanNameAware
     * 此处传递的是Spring配置文件中Bean的id值
     *
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        System.err.println("2.1、通过BeanNameAware初始化执行....  name:" + name);
    }

    /**
     * BeanFactoryAware
     * 此处传递的是Spring工厂自身
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.err.println("2.2、通过BeanFactoryAware初始化执行....  beanFactory:" + beanFactory);
    }

    /**
     * ApplicationContextAware
     * 此处传递的是Spring的上下文
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.err.println("2.3、通过ApplicationContextAware初始化执行....  applicationContext:" + applicationContext);
    }

    /**
     * InitializingBean
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.err.println("2.5、通过InitializingBean初始化执行....");
    }

    /**
     * BeanPostProcessor
     * 初始化预处理:被用作Bean内容修改，并且由于这个是在Bean初始化结束的时候调用的这个方法，也可以被用于内存或缓存技术。
     */
    /*@Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.err.println("....通过BeanPostProcessor Before初始化执行....  bean:" + bean + ",beanName:" + beanName);
        return bean;
    }*/

    /**
     * BeanPostProcessor
     * 初始化预处理:被用作Bean内容修改，并且由于这个是在Bean初始化结束的时候调用的这个方法，也可以被用于内存或缓存技术。
     */
    /*@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.err.println("....通过BeanPostProcessor After初始化执行....  bean:" + bean + ",beanName:" + beanName);
        return bean;
    }*/

    @PreDestroy  //destroy-method
    public void mydestory() {
        System.err.println("1、通过@PreDestroy执行....");
    }

    /**
     * DisposableBean
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        System.err.println("2、通过DisposableBean执行....");
    }

}
