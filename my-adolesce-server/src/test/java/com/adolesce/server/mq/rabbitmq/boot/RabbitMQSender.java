package com.adolesce.server.mq.rabbitmq.boot;

import cn.hutool.core.lang.UUID;
import com.adolesce.common.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 简单模式
     */
    @Test
    public void sendStrToHelloQueue() {
        String context = "hello " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.err.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("boot-hello-queue", context);
    }

    /**
     * 工作模式：发送多条消息，消费者均匀平摊消费消息
     */
    @Test
    public void sendObjectsToWorkQueue(){
        User user;
        for (int i = 1; i <= 10; i++) {
            user = new User();
            String uuid = UUID.randomUUID().toString();
            user.setSeriNo(uuid);
            user.setUserName("张三" + i);
            user.setSex(1);
            user.setIsOld(false);
            //发送的消息中携带CorrelationData（消息唯一标识），如果发送消息确认失败，方便排查Bug
            this.rabbitTemplate.convertAndSend("boot-work-queue", user, message -> {
                //设置该条消息持久化
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;

            }, new CorrelationData(uuid));

            System.err.println("Sender : " + user);
        }
    }

    /**
     * 广播模式
     */
    @Test
    public void sendStrToFanoutExchange() {
        String context = "hello " + new Date();
        System.err.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("boot-log-fanout-exchange", "", context);
    }

    /**
     * 路由模式
     */
    @Test
    public void sendStrToDirectExchange() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("boot-log-direct-exchange", "info", context + " info");
        this.rabbitTemplate.convertAndSend("boot-log-direct-exchange", "warning", context + " warning");
        this.rabbitTemplate.convertAndSend("boot-log-direct-exchange", "error", context + " error");
    }

    /**
     * 主题模式
     */
    @Test
    public void sendStrToTopicExchange() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("boot-log-topic-exchange", "123.critical", context + " 123.critical");
        this.rabbitTemplate.convertAndSend("boot-log-topic-exchange", "kernel.critical", context + " kernel.critical");
        this.rabbitTemplate.convertAndSend("boot-log-topic-exchange", "kernel.123", context + " kernel.123");
    }

    @Test
    public void sendObjectToTopicExchange() {
        User user = new User();
        user.setUserName("张三");
        user.setSex(1);
        user.setIsOld(false);
        System.err.println("Sender : " + user);
        this.rabbitTemplate.convertAndSend("boot-log-topic-exchange2", "abc", user);
        //this.rabbitTemplate.convertSendAndReceive("boot-log-topic-exchange2", "audit.b.c.d", user);
        //convertSendAndReceive：使用此方法，只有确定消费者接收到消息，才会发送下一条信息，每条消息之间会有间隔时间
    }
}