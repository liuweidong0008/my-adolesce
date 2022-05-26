package com.adolesce.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig{
    /**
     * 自定义RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        //设置消息投递至exchange的回调
        rabbitTemplate.setConfirmCallback(new ConfirmCallbackHandler());
        //设置消息从exchage路由至queue的回调
        rabbitTemplate.setReturnCallback(new ReturnCallBackHandler());
        //想要触发从exchage路由至queue的回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        //设置消息体转换器(可读性更强，效率更高)
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 消息投递至交换机的回调（每次消息发送都会触发），需要在配置文件中开启：publisher-confirm-type: correlated
     *      如果消息没有投递到 exchange, isSuccess = false
     *      如果消息投递到 exchange, isSuccess = true
     */
    public class ConfirmCallbackHandler implements RabbitTemplate.ConfirmCallback {
        /**
         * @param correlationData 消息相关数据，一般用于获取 唯一标识 id
         * @param isSuccess 消息投递至exchage是否成功成功，true：成功  false：失败
         * @param cause 投递至exchange失败的原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean isSuccess, String cause) {
            if (isSuccess) {
                String data = correlationData != null ? correlationData.getId():"";
                System.err.println("【全局】消息成功投递到交换机！消息ID: " + data);
            } else {
                //可进行消息重发
                String data = correlationData != null ? correlationData.getId():"";
                System.err.println("【全局】消息投递到交换机失败！消息ID: " + data +  " cause: " + cause);
            }
        }
    }

    /**
     * 消息从交换机路由至队列的回调（消息到达exchange，但是没有到queue就会回调）
     *      必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
     *      publisher-returns: true
     *      template:
     *          mandatory: true
     */
    public class ReturnCallBackHandler implements RabbitTemplate.ReturnCallback {
        /**
         * @param message  消息主体
         * @param replyCode  错误状态码
         * @param replyText 错误状态码对应的文本信息
         * @param exchange 消息使用的交换器
         * @param routingKey 消息使用的路由键
         */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            // 判断是否是延迟消息
            Integer receivedDelay = message.getMessageProperties().getReceivedDelay();
            if (receivedDelay != null && receivedDelay > 0) {
                // 是一个延迟消息，忽略这个错误提示
                return;
            }
            // 记录日志
            log.error("消息发送到队列失败，响应码：{}, 失败原因：{}, 交换机: {}, 路由key：{}, 消息: {}",
                    replyCode, replyText, exchange, routingKey, message.toString());
            System.err.println("消息主体 message : "+new String(message.getBody()));
            System.err.println("应答码 : "+replyCode);
            System.err.println("原因描述："+replyText);
            System.err.println("消息使用的交换器 exchange : "+exchange);
            System.err.println("消息使用的路由键 routing : "+routingKey);
            // 如果有需要的话，重发消息
        }
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
}