package com.adolesce.server.rocketmq.messagetype;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

//测试消息的种类
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        producer.start();
        for (int i = 1; i <= 5; i++) {
            //同步消息发送
//            Message msg = new Message("topic2",("同步消息：hello rocketmq "+i).getBytes("UTF-8"));
//            SendResult result = producer.send(msg);
//            System.out.println("返回结果："+result);

            //异步消息发送
            /*Message msg = new Message("topic2",("异步消息：hello rocketmq "+i).getBytes("UTF-8"));
            producer.send(msg, new SendCallback() {
                //表示成功返回结果
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult);
                }
                //表示发送消息失败
                public void onException(Throwable t) {
                    System.out.println(t);
                }
            },10000);*/

            //单向消息
            Message msg = new Message("topic2", ("单向消息：hello rocketmq " + i).getBytes("UTF-8"));
            producer.sendOneway(msg);
        }
        //添加一个休眠操作，确保异步消息返回后能够输出
        TimeUnit.SECONDS.sleep(10);

        producer.shutdown();
    }
}
