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
    public Queue helloQueue() {
        return new Queue("boot-hello-queue");
    }

    /**
     * 工作模式
     */
    @Bean
    public Queue workQueue() {
        return new Queue("boot-work-queue");
    }

    /**
     * Fanout广播模式
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("boot-log-fanout-exchange");
    }

    @Bean
    public Queue fanoutQueue1() {
        return new Queue("boot-log-fanout-queue1");
    }

    @Bean
    public Queue fanoutQueue2() {
        return new Queue("boot-log-fanout-queue2");
    }

    //将log-fanout-queue1、log-fanout-queue2两个队列绑定到boot-log-fanout-exchange交换机上面，发送端的routing_key写任何字符都会被忽略：
    @Bean
    public Binding bindingFanoutExchangeAndFanoutQueue1(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean
    public Binding bindingFanoutExchangeAndFanoutQueue2() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    /**
     * Direct路由模式
     */
    @Bean
    public DirectExchange directexchange() {
        return new DirectExchange("boot-log-direct-exchange");
    }

    @Bean
    public Queue directQueue1() {
        return new Queue("boot-log-direct-queue-info");
    }

    @Bean
    public Queue directQueue2() {
        return new Queue("boot-log-direct-queue-warn");
    }

    @Bean
    public Queue directQueue3() {
        return new Queue("boot-log-direct-queue-error");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue1(DirectExchange directexchange, Queue directQueue1) {
        return BindingBuilder.bind(directQueue1).to(directexchange).with("info");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue2(DirectExchange directexchange, Queue directQueue2) {
        return BindingBuilder.bind(directQueue2).to(directexchange).with("warning");
    }

    @Bean
    public Binding bindingDirectExchangeAndDirectQueue3(DirectExchange directexchange, Queue directQueue3) {
        return BindingBuilder.bind(directQueue3).to(directexchange).with("error");
    }

    /**
     * Topic 主题模式（*表示一个词  #表示零个或多个词）
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("boot-log-topic-exchange");
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue("boot-log-topic-queue1");
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue("boot-log-topic-queue2");
    }


    @Bean
    public Binding bindingTopicExchangeAndTopicQueue1(TopicExchange topicExchange, Queue topicQueue1) {
        return BindingBuilder.bind(topicQueue1).to(topicExchange).with("*.critical");
    }

    @Bean
    public Binding bindingTopicExchangeAndTopicQueue2(TopicExchange topicExchange, Queue topicQueue2) {
        return BindingBuilder.bind(topicQueue2).to(topicExchange).with("kernel.*");
    }

}