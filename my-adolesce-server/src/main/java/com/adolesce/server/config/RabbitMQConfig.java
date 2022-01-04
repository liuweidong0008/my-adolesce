package com.adolesce.server.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig{
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

    /**
     * Direct路由模式
     */
    @Bean
    public DirectExchange directexchange() {
        return new DirectExchange("boot-log-direct-exchange");
    }

    /**
     * Topic 主题模式（*表示一个词  #表示零个或多个词）
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("boot-log-topic-exchange");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        /**
         * 配置消息回调，目前回调存在ConfirmCallback和ReturnCallback两者。他们的区别在于：
         *     1、如果消息没有到exchange,则ConfirmCallback回调,ack=false
         *     2、如果消息到达exchange,则ConfirmCallback回调,ack=true
         *     3、exchange到queue未成功,则触发回调ReturnCallback（必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调）
         */
        rabbitTemplate.setConfirmCallback(new ConfirmCallbackHandler());
        rabbitTemplate.setReturnCallback(new ReturnCallBackHandler());
        // 想要触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public ConfirmCallbackHandler confirmCallbackHandler() {
        return new ConfirmCallbackHandler();
    }

    /**
     * 每次消息发送都会触发
     */
    public class ConfirmCallbackHandler implements RabbitTemplate.ConfirmCallback {
        /**
         * @param correlationData 消息相关的数据，一般用于获取 唯一标识 id
         * @param isSuccess true 消息确认成功，false 失败
         * @param cause 确认失败的原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean isSuccess, String cause) {
            if (isSuccess) {
                String data = correlationData != null ? correlationData.getId():"";
                System.err.println("confirm 消息确认成功..." + data);
            } else {
                //可进行消息重发
                String data = correlationData != null ? correlationData.getId():"";
                System.err.println("confirm 消息确认失败..." +  data + " cause: " + cause);
            }
        }
    }

    /**
     *  return 的回调方法（找不到路由才会触发）
     */
    @Bean
    public ReturnCallBackHandler returnCallBackHandler() {
        return new ReturnCallBackHandler();
    }

    public class ReturnCallBackHandler implements RabbitTemplate.ReturnCallback {
        /**
         * 消息到达exchange，但是没有到queue就会回调
         * @param message  消息主体
         * @param replyCode  错误状态码
         * @param replyText 错误状态码对应的文本信息
         * @param exchange 消息使用的交换器
         * @param routingKey 消息使用的路由键
         */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("消息主体 message : "+message);
            System.err.println("消息主体 message : "+replyCode);
            System.err.println("描述："+replyText);
            System.err.println("消息使用的交换器 exchange : "+exchange);
            System.err.println("消息使用的路由键 routing : "+routingKey);
        }
    }


}