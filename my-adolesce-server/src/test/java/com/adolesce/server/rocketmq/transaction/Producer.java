package com.adolesce.server.rocketmq.transaction;

import com.adolesce.server.rocketmq.order.domain.RocketConfig;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

//测试事务消息
public class Producer {
    public static void main(String[] args) throws Exception {
        //事务消息使用的生产者是TransactionMQProducer
        TransactionMQProducer producer = new TransactionMQProducer(RocketConfig.GROUP1);
        producer.setSendMsgTimeout(10000);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        //......transactionId
        //添加本地事务对应的监听
        producer.setTransactionListener(new TransactionListener() {
            //正常事务过程
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //中间状态
                return LocalTransactionState.UNKNOW;
            }

            //事务补偿过程
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                System.out.println("事务补偿过程执行");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();

        Message msg = new Message("topic10", ("事务消息：hello rocketmq transactionId").getBytes("UTF-8"));
        TransactionSendResult result = producer.sendMessageInTransaction(msg, null);
        System.out.println("返回结果：" + result);
        //事务补偿过程必须保障服务器在运行过程中，否则将无法进行正常的事务补偿
        //producer.shutdown();
    }

    public static void main3(String[] args) throws Exception {
        //事务消息使用的生产者是TransactionMQProducer
        TransactionMQProducer producer = new TransactionMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        //添加本地事务对应的监听
        producer.setTransactionListener(new TransactionListener() {
            //正常事务过程
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //事务提交状态
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            //事务补偿过程
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                return null;
            }
        });
        producer.start();

        Message msg = new Message("topic8", ("事务消息：hello rocketmq ").getBytes("UTF-8"));
        TransactionSendResult result = producer.sendMessageInTransaction(msg, null);
        System.out.println("返回结果：" + result);
        producer.shutdown();
    }

    public static void main2(String[] args) throws Exception {
        //事务消息使用的生产者是TransactionMQProducer
        TransactionMQProducer producer = new TransactionMQProducer(RocketConfig.GROUP1);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        producer.setSendMsgTimeout(10000);
        //添加本地事务对应的监听
        producer.setTransactionListener(new TransactionListener() {
            //正常事务过程
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //事务回滚状态
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            //事务补偿过程
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                return null;
            }
        });
        producer.start();

        Message msg = new Message("topic9", ("事务消息：hello rocketmq ").getBytes("UTF-8"));
        TransactionSendResult result = producer.sendMessageInTransaction(msg, null);
        System.out.println("返回结果：" + result);
        producer.shutdown();
    }
}