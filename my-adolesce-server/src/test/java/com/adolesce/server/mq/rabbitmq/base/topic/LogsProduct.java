package com.adolesce.server.mq.rabbitmq.base.topic;

import com.adolesce.server.mq.rabbitmq.base.RabbitFactory;
import com.adolesce.server.utils.rabbitmq.TopicSender;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 16:51
 */
public class LogsProduct extends RabbitFactory {

    //主题模式演示
    @Test
    public void sendLogsToTopicDemo(){
        //routingKey
        String[] routing_keys = new String[] { "kernel.info", "cron.warning",
                "auth.info", "kernel.critical" };
        //发送3条不同routingKey绑定的消息
        for (String routing_key : routing_keys)
        {
            String message =routing_key + "  "+ UUID.randomUUID().toString();
            try {
                TopicSender.send("log-topic-exchange", routing_key, message,true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(" [x] Sent routingKey = "+routing_key+" ,msg = " + message + ".");
        }
    }
}
