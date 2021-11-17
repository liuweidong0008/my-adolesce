package com.adolesce.mqconsumer.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableRabbit
public class RabbitMQConfig/* implements RabbitListenerConfigurer */{
    /**
     * 简单模式
     */
    @Bean
    public Queue queueHelloQueue() {
        return new Queue("boot-hello-queue");
    }

    /**
     * 工作模式
     */
    @Bean
    public Queue queueWorkQueue() {
        return new Queue("boot-work-queue");
    }

    /**
     * Fanout广播模式
     */
    @Bean
    public FanoutExchange exchangeFanout() {
        return new FanoutExchange("boot-log-fanout-exchange");
    }

    @Bean
    public Queue queueFanoutQueue1() {
        return new Queue("boot-log-fanout-queue1");
    }

    @Bean
    public Queue queueFanoutQueue2() {
        return new Queue("boot-log-fanout-queue2");
    }

    //将log-fanout-queue1、log-fanout-queue2两个队列绑定到boot-log-fanout-exchange交换机上面，发送端的routing_key写任何字符都会被忽略：
    @Bean
    public Binding bindingFanoutExchangeAndFanoutQueue1(FanoutExchange exchangeFanout, Queue queueFanoutQueue1) {
        return BindingBuilder.bind(queueFanoutQueue1).to(exchangeFanout);
    }

    @Bean
    public Binding bindingFanoutExchangeAndFanoutQueue2(FanoutExchange exchangeFanout, Queue queueFanoutQueue2) {
        return BindingBuilder.bind(queueFanoutQueue2).to(exchangeFanout);
    }

    /**
     * Direct路由模式
     */
    @Bean
    public DirectExchange exchangeDirect() {
        return new DirectExchange("boot-log-direct-exchange");
    }

    @Bean
    public Queue queueDirectQueue1() {
        return new Queue("boot-log-direct-queue-info");
    }

    @Bean
    public Queue queueDirectQueue2() {
        return new Queue("boot-log-direct-queue-warn");
    }

    @Bean
    public Queue queueDirectQueue3() {
        return new Queue("boot-log-direct-queue-error");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue1(DirectExchange exchangeDirect, Queue queueDirectQueue1) {
        return BindingBuilder.bind(queueDirectQueue1).to(exchangeDirect).with("info");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue2(DirectExchange exchangeDirect, Queue queueDirectQueue2) {
        return BindingBuilder.bind(queueDirectQueue2).to(exchangeDirect).with("warning");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue3(DirectExchange exchangeDirect, Queue queueDirectQueue3) {
        return BindingBuilder.bind(queueDirectQueue3).to(exchangeDirect).with("error");
    }

    /**
     * Topic 主题模式（*表示一个词  #表示零个或多个词）
     */
    @Bean
    public TopicExchange exchangeTopic() {
        return new TopicExchange("boot-log-topic-exchange");
    }

    @Bean
    public Queue queueTopicQueue1() {
        return new Queue("boot-log-topic-queue1");
    }

    @Bean
    public Queue queueTopicQueue2() {
        return new Queue("boot-log-topic-queue2");
    }


    @Bean
    public Binding bindingTopicExchangeAndTopicQueue1(TopicExchange exchangeTopic, Queue queueTopicQueue1) {
        return BindingBuilder.bind(queueTopicQueue1).to(exchangeTopic).with("*.critical");
    }

    @Bean
    public Binding bindingTopicExchangeAndTopicQueue2(TopicExchange exchangeTopic, Queue queueTopicQueue2) {
        return BindingBuilder.bind(queueTopicQueue2).to(exchangeTopic).with("kernel.*");
    }

}