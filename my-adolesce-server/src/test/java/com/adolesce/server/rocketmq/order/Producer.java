package com.adolesce.server.rocketmq.order;

import com.adolesce.server.rocketmq.order.domain.Order;
import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;

//测试顺序消息
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        producer.start();

        //创建要执行的业务队列
        List<Order> orderList = new ArrayList<Order>();

        Order order11 = new Order();
        order11.setId("a");
        order11.setMsg("主单-1");
        orderList.add(order11);

        Order order12 = new Order();
        order12.setId("a");
        order12.setMsg("子单-2");
        orderList.add(order12);

        Order order13 = new Order();
        order13.setId("a");
        order13.setMsg("支付-3");
        orderList.add(order13);

        Order order14 = new Order();
        order14.setId("a");
        order14.setMsg("推送-4");
        orderList.add(order14);

        Order order21 = new Order();
        order21.setId("b");
        order21.setMsg("主单-1");
        orderList.add(order21);

        Order order22 = new Order();
        order22.setId("b");
        order22.setMsg("子单-2");
        orderList.add(order22);

        Order order31 = new Order();
        order31.setId("c");
        order31.setMsg("主单-1");
        orderList.add(order31);

        Order order32 = new Order();
        order32.setId("c");
        order32.setMsg("子单-2");
        orderList.add(order32);

        Order order33 = new Order();
        order33.setId("c");
        order33.setMsg("支付-3");
        orderList.add(order33);

        //设置消息进入到指定的消息队列中
        for(final Order order : orderList){
            Message msg = new Message("orderTopic",order.toString().getBytes());
            //发送时要指定对应的消息队列选择器
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                //设置当前消息发送时使用哪一个消息队列
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    //根据发送的信息不同，选择不同的消息队列
                    //根据id来选择一个消息队列的对象，并返回->id得到int值
                    int mqIndex = order.getId().hashCode() % list.size();
                    return list.get(mqIndex);
                }
            }, null);

            System.out.println(result);
        }

        producer.shutdown();
    }
}