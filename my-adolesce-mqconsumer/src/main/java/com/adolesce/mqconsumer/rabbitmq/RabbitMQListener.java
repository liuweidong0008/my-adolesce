package com.adolesce.mqconsumer.rabbitmq;

import com.adolesce.common.entity.User;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
    public void workQueueConsumer1(User user) {
        System.err.println("work-queue队列消费者1 消费消息：" + user);
    }

    @RabbitListener(queues = {"boot-work-queue"})
    public void workQueueConsumer2(User user) {
        System.err.println("work-queue队列消费者2 消费消息：" + user);
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
    public void topicQueue1Consumer(String message) {
        System.err.println("log-topic-queue1队列消费者 消费消息：" + message);
    }

    @RabbitListener(queues = {"boot-log-topic-queue2"})
    public void topicQueue2Consumer(String message) {
        System.err.println("log-topic-queue2队列消费者 消费消息：" + message);
    }

    /**
     * 在方法上一次性进行队列声明、交换机声明、绑定关系声明
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "boot-log-topic-queue3",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = "boot-log-topic-exchange3",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = "audit.#"
            )
    )
    public void audit(User user) {
        System.out.println("log-topic-queue3队列消费者 消费消息：" + user);
    }

}