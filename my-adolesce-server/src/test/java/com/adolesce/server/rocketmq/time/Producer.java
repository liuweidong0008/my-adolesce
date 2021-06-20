package com.adolesce.server.rocketmq.time;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

//测试延时消息
/*MQ内部维护了18个延时级别，角标顺序如下：
        1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h*/
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.start();
        for (int i = 1; i <= 5; i++) {
            Message msg = new Message("topic3",("非延时消息：hello rocketmq "+i).getBytes("UTF-8"));
            //设置当前消息的延时效果
            msg.setDelayTimeLevel(2);
            SendResult result = producer.send(msg);
            System.out.println("返回结果："+result);
        }
        producer.shutdown();
    }
}