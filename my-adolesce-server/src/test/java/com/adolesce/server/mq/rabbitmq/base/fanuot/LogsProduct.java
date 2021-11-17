package com.adolesce.server.mq.rabbitmq.base.fanuot;

import com.adolesce.server.mq.rabbitmq.base.RabbitFactory;
import com.adolesce.server.utils.rabbitmq.FanoutSender;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 16:51
 */
public class LogsProduct extends RabbitFactory {

    //发布、订阅模式演示
    @Test
    public void sendLogsToFanoutDemo(){
        //发送的消息
        String message = "";
        try {
            for (int i = 1; i <= 6; i++) {
                message = "log something ...";
                message = message + i;
                FanoutSender.send("log-fanout-exchange", message,true);
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
