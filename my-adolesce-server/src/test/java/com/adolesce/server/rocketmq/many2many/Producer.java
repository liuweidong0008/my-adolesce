package com.adolesce.server.rocketmq.many2many;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

//多生产者对多消费者
//生产者，产生消息
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        producer.start();
        for (int i = 1; i <= 10; i++) {
            Message msg = new Message("topic1", ("生产者2： hello rocketmq " + i).getBytes("UTF-8"));
            SendResult result = producer.send(msg, 10000);
            System.out.println("返回结果：" + result);
        }
        producer.shutdown();
    }
}
