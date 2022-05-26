package com.adolesce.mqconsumer.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableRabbit
public class RabbitMQConfig/* implements RabbitListenerConfigurer */{
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

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
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
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

    /**
     * 测试消费者消息确认模式为AUTO模式，开启消息重试，重试次数用完后的失败策略：RepublishMessageRecoverer
     * 1、定义接收失败消息的交换机、队列及其绑定关系
     * 2、定义 RepublishMessageRecoverer
     */
    @Bean
    public DirectExchange errorMessageExchange(){
        return new DirectExchange("error.direct");
    }

    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue");
    }

    @Bean
    public Binding errorMessageBinding(){
        return BindingBuilder.bind(errorQueue()).to(errorMessageExchange()).with("error");
    }

    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }

    /**
     * TTL + 死信队列 实现延时消息-【声明一组普通交换机和队列】
     */
    @Bean
    public DirectExchange ttlDirectExchange(){
        return new DirectExchange("ttl.direct");
    }

    @Bean
    public Queue ttlQueue(){
        return QueueBuilder
                .durable("ttl.queue")  // 指定队列名称，并持久化
                .ttl(10000)     //设置队列的超时时间，10秒
                .deadLetterExchange("dl.direct")     // 指定死信交换机
                .deadLetterRoutingKey("dl")      //指定死信routingKey
                .build();
    }

    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlDirectExchange()).with("ttl");
    }

    /**
     * TTL + 死信队列 实现延时消息-【声明一组死信交换机和队列】
     */
    @Bean
    public DirectExchange dlMessageExchange(){
        return new DirectExchange("dl.direct");
    }

    @Bean
    public Queue dlQueue(){
        return new Queue("dl.queue");
    }

    @Bean
    public Binding dlMessageBinding(){
        return BindingBuilder.bind(dlQueue()).to(dlMessageExchange()).with("dl");
    }




    /**
     * DelayExchange 延时插件实现 延时消息
     */
    @Bean
    public DirectExchange delayedExchange(){
        return ExchangeBuilder.directExchange("delay.direct").delayed().build();
    }

    @Bean
    public Queue delayQueue(){
        return new Queue("delay.queue");
    }

    @Bean
    public Binding delayedBinding(){
        return BindingBuilder.bind(delayQueue()).to(delayedExchange()).with("delay");
    }

    /**
     * 声明惰性队列（两种方式）
     * 1、基于@Bean声明lazy-queue
     * 2、基于@RabbitListener声明LazyQueue
     */
    @Bean
    public Queue lazyQueue() {
        return QueueBuilder.durable("lazy.queue")
                .lazy()  //开启x-queue-mode为lazy
                .build();
    }

    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable("normal.queue")
                .build();
    }
}