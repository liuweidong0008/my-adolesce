package com.adolesce.server.mq.rabbitmq.boot;

import cn.hutool.core.lang.UUID;
import com.adolesce.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
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
        for (int i = 1; i <= 4; i++) {
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

            //另一种设置消息持久化的方式
            // 1.准备消息
           /* Message message2 = MessageBuilder.withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();

            this.rabbitTemplate.convertAndSend("boot-work-queue", message2, new CorrelationData(uuid));*/

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
        this.rabbitTemplate.convertAndSend("boot-log-topic-exchange", "kernel.123", context + " kernel.123");
        this.rabbitTemplate.convertSendAndReceive("boot-log-topic-exchange", "audit.b.c.d", context + " audit.b.c.d");
        //convertSendAndReceive：使用此方法，只有确定消费者接收到消息，才会发送下一条信息，每条消息之间会有间隔时间
    }

    /**
     * 生产者确认机制测试：
     *   1、测试消息未发送至交换机的回调；
     *   2、测试消息发送至交换机的回调；
     *   3、测试消息发送至交换机、未路由至队列的回调；
     */
    @Test
    public void testSenderComfirm() {
        // 1.准备消息
        String message = "hello, spring amqp!";
        // 2.准备CorrelationData
        // 2.1.消息ID
        CorrelationData correlationData = new CorrelationData(java.util.UUID.randomUUID().toString());
        // 2.2.准备ConfirmCallback
        correlationData.getFuture().addCallback(result -> {
            // 判断结果
            if (result.isAck()) {
                // ACK
                System.err.println("【局部】消息成功投递到交换机！消息ID: " + correlationData.getId());
            } else {
                // NACK
                System.err.println("【局部】消息投递到交换机失败！消息ID: " + correlationData.getId());
                // 重发消息
            }
        }, ex -> {
            // 记录日志
            System.err.println("【局部】消息发送失败！异常信息: " + ex);
            log.error("消息发送失败！", ex);
            // 重发消息
        });
        // 3.发送消息
        rabbitTemplate.convertAndSend("boot-log-direct-exchange", "info", message, correlationData);
    }

    /**
     * 延时消息测试【给队列设置了消息存活时间】
     */
    @Test
    public void testTTLMsg1() {
        // 1.准备消息
        String message = "hello, TTL MSG!";
        rabbitTemplate.convertAndSend("ttl.direct", "ttl", message);
        log.info("延时消息已经成功发送！");
    }

    /**
     * 延时消息测试【给消息本身设置存活时间】
     */
    @Test
    public void testTTLMsg2() {
        /*Message message = MessageBuilder
                .withBody("hello, ttl messsage".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setExpiration("5000")
                .build();
        rabbitTemplate.convertAndSend("ttl.direct", "ttl",message);*/

        // 1.发送消息
        rabbitTemplate.convertAndSend("ttl.direct", "ttl", "hello, ttl messsage", message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //设置消息存活时间为5秒
            messageProperties.setExpiration("5000");
            return message;
        });
        // 2.记录日志
        log.info("延时消息已经成功发送！");
    }

    /**
     * 延时消息测试【通过延时交换机插件实现】
     */
    @Test
    public void testDelayMsg() {
        /*Message message = MessageBuilder
                .withBody("hello, ttl messsage".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setHeader("x-delay",7000)
                .build();
        rabbitTemplate.convertAndSend("delay.direct", "delay",message);*/

        // 1.发送消息
        rabbitTemplate.convertAndSend("delay.direct", "delay", "hello, delay messsage", message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //设置消息存活时间为7秒
            messageProperties.setHeader("x-delay",7000);
            return message;
        });
        // 2.记录日志
        log.info("延时消息已经成功发送！");
    }

    /**
     * 测试惰性队列
     */
    @Test
    public void testLazyQueue() {
        long b = System.nanoTime();
        // 1.准备消息
        MessagePostProcessor message = msg -> {
            msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
            return msg;
        };
        Object content = "hello, Spring";
        for (int i = 0; i < 1000000; i++) {
            // 2.发送消息
            rabbitTemplate.convertAndSend("lazy.queue", content,  message);
        }
        long e = System.nanoTime();
        System.out.println(e - b);
    }

    @Test
    public void testNormalQueue() {
        long b = System.nanoTime();
        // 1.准备消息
        MessagePostProcessor message = msg -> {
            msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
            return msg;
        };
        for (int i = 0; i < 1000000; i++) {
            // 2.发送消息
            rabbitTemplate.convertAndSend("normal.queue", (Object) "hello, Spring",  message);
        }
        long e = System.nanoTime();
        System.out.println(e - b);
    }
}