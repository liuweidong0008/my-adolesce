package com.adolesce.server.rocketmq.filterbytag;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

//测试按照tag过滤消息
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        producer.start();

        //创建消息的时候除了制定topic，还可以指定tag
        Message msg = new Message("topic6","tag1",("消息过滤按照tag：hello rocketmq 1").getBytes("UTF-8"));

        SendResult send = producer.send(msg);
        System.out.println(send);

        producer.shutdown();
    }
}