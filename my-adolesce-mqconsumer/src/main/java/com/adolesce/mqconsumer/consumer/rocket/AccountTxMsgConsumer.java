package com.adolesce.mqconsumer.consumer.rocket;

import com.adolesce.common.config.RocketConfig;
import com.adolesce.common.vo.AccountChangeEvent;
import com.adolesce.mqconsumer.service.AccountInfoService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class AccountTxMsgConsumer {
    @Autowired
    private AccountInfoService accountInfoService;

    @PostConstruct
    public void consumerTxMsg() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketConfig.GROUP1);
        consumer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        consumer.subscribe("accountTxTopic","*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    for(MessageExt msg : list){
                        String plusAmountMsg = new String(msg.getBody());
                        log.info("开始消费消息:{}",plusAmountMsg);
                        JSONObject jsonObject = JSONObject.parseObject(plusAmountMsg);
                        AccountChangeEvent ace = JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
                        //为某个账户转账
                        accountInfoService.addAccountInfoBalance(ace);
                        System.out.println("消息："+plusAmountMsg);
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        consumer.start();
        System.out.println("接收消息服务已开启运行");
    }
}
