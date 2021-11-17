package com.adolesce.server.mq.rabbitmq.base.basic;

import com.adolesce.server.mq.rabbitmq.base.RabbitFactory;
import com.adolesce.server.utils.rabbitmq.BasicSender;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 16:09
 */
public class WorkQueueProduct extends RabbitFactory {

    //多消费者演示
    @Test
    public void workQueueProductDemo(){
        try {
            //发送10条消息，依次在消息后面附加1-10个点
            for (int i = 9; i >= 0; i--)  //for (int i = 0; i < 10; i++)
            {
                String dots = "";
                for (int j = 0; j <= i; j++)
                {
                    dots += ".";
                }
                String message = "helloworld" + dots+dots.length();
                BasicSender.send("work-queue", message,true); //持久化
                System.out.println(" [x] Sent '" + message + "'");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
