package com.adolesce.mqconsumer.rocketmq;

import org.springframework.stereotype.Component;

@Component
//@RocketMQMessageListener(topic = "adolesce-topic", consumerGroup = "group2")
public class Consumer{ //implements RocketMQListener<String> {
   // @Override
    public void onMessage(String s) {
        System.out.println("收到消息： " + s);
    }
}