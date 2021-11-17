package com.adolesce.server.mq.rabbitmq.base.direct;

import com.adolesce.server.mq.rabbitmq.base.RabbitFactory;
import com.adolesce.server.utils.rabbitmq.DirectRoutingSender;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 16:51
 */
public class LogsProduct extends RabbitFactory {

    //路由模式演示
    @Test
    public void sendLogsToDirectDemo(){
        for (int i = 1; i <= 6; i++){
            String severity = getSeverity();
            String message = severity + "_log :" + UUID.randomUUID().toString();
            try {
                DirectRoutingSender.send("log-direct-exchange", severity, message,true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(" [x] Sent '" + message + "'");
        }
    }

    private static final String[] SEVERITIES = { "info", "warning", "error" };
    /**
     * 随机产生一种日志类型
     *
     * @return
     */
    private String getSeverity()
    {
        Random random = new Random();
        int ranVal = random.nextInt(3);
        return SEVERITIES[ranVal];
    }
}
