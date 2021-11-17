package com.adolesce.server.mq.rabbitmq.base.basic;

import com.adolesce.server.mq.rabbitmq.base.RabbitFactory;
import com.adolesce.server.utils.rabbitmq.BasicSender;
import org.junit.Test;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 16:07
 */
public class HelloProduct extends RabbitFactory {

    //单消费者演示
    @Test
    public void helloProductDemo(){
        //发送的消息
        String message = "hello world!";
        try {
            BasicSender.send("hello-queue",message,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" [x] Sent '" + message + "'");
    }
}
