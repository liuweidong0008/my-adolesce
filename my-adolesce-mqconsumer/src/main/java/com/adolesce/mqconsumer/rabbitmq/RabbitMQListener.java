package com.adolesce.mqconsumer.rabbitmq;

import com.adolesce.common.entity.User;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RabbitMQListener {
    /**
     * 简单模式
     */
    @RabbitListener(queues = {"boot-hello-queue"})
    public void helloQueueConsumer(String message){
        System.err.println("hello-queue队列-消费者 消费消息：" + message);
    }

    /**
     * 工作模式
     */
    @RabbitListener(queues = {"boot-work-queue"})
    public void workQueueConsumer2(User user) {
        System.err.println("work-queue队列消费者2 消费消息：" + user);
    }


    @RabbitListener(queues = {"boot-work-queue"})
    public void workQueueConsumer1(User user, Channel channel, Message message) throws IOException {
        try {
            //业务处理代码
            System.err.println("work-queue队列消费者1 消费消息：" + user);

            /**
             * 手动ACK 确认一条消息
             * channel.basicAck(deliveryTag, false);
             * deliveryTag:该消息的index
             * multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //消费者处理出了问题，需要告诉队列信息消费失败
            /**
             * 拒绝确认消息:<br>
             * channel.basicNack(long deliveryTag, boolean multiple, boolean requeue) ; <br>
             * deliveryTag:该消息的index<br>
             * multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。<br>
             * requeue：被拒绝的是否重新入队列 <br>
             */
            log.error("消息即将再次返回队列处理...", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);

            /**
             * 拒绝一条消息：<br>
             * channel.basicReject(long deliveryTag, boolean requeue);<br>
             * deliveryTag:该消息的index<br>
             * requeue：被拒绝的是否重新入队列
             */
            /*log.error("消息已重复处理失败,拒绝再次接收...", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); */
        }
    }


    /**
     * 广播模式
     */
    @RabbitListener(queues = {"boot-log-fanout-queue1"})
    public void fanoutQueue1Consumer(String message) {
        System.err.println("log-fanout-queue1队列消费者 消费消息：" + message);
    }

    @RabbitListener(queues = {"boot-log-fanout-queue2"})
    public void fanoutQueue2Consumer(String message) {
        System.err.println("log-fanout-queue2队列消费者 消费消息：" + message);
    }

    /**
     * 路由模式
     */
    @RabbitListener(queues = {"boot-log-direct-queue-info"})
    public void directQueieInfoConsumer(String message) {
        System.err.println("log-direct-queue-info队列消费者 消费消息：" + message);
    }

    @RabbitListener(queues = {"boot-log-direct-queue-warn"})
    public void directQueieWarnConsumer(String message) {
        System.err.println("log-direct-queue-warn队列消费者 消费消息：" + message);
    }

    @RabbitListener(queues = {"boot-log-direct-queue-error"})
    public void directQueieErrorConsumer(String message) {
        System.err.println("log-direct-queue-error队列消费者 消费消息：" + message);
    }

    /**
     *
     * 主题模式
     */
    @RabbitListener(queues = {"boot-log-topic-queue1"})
    public void topicQueue1Consumer(String msg,Channel channel, Message message) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        System.err.println("log-topic-queue1队列消费者 消费消息：" + msg);
    }

    @RabbitListener(queues = {"boot-log-topic-queue2"})
    public void topicQueue2Consumer(String msg,Channel channel, Message message) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        System.err.println("log-topic-queue2队列消费者 消费消息：" + msg);
    }

    /**
     * 在方法上一次性进行队列声明、交换机声明、绑定关系声明
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "boot-log-topic-queue3",
                            durable = "true",
                            exclusive = "false",
                            autoDelete = "false"
                    ),
                    exchange = @Exchange(
                            value = "boot-log-topic-exchange2",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = "audit.#"
            )
    )
    public void audit(User user) {
        //int i = 1/0;
        System.out.println("log-topic-queue3队列消费者 消费消息：" + user);
    }

}