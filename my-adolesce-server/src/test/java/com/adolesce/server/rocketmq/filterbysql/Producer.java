package com.adolesce.server.rocketmq.filterbysql;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

//测试按照sql过滤消息
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        producer.start();

        Message msg = new Message("topic7", ("消息过滤按照sql：hello rocketmq").getBytes("UTF-8"));
        //为消息添加属性
        msg.putUserProperty("vip", "2");
        msg.putUserProperty("age", "16");

        SendResult send = producer.send(msg);
        System.out.println(send);

        producer.shutdown();
    }
}