package com.adolesce.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.adolesce.cloud.dubbo.api.db.AccountInfoApi;
import com.adolesce.common.config.RocketConfig;
import com.adolesce.common.vo.AccountChangeEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AccountInfoService {
    @DubboReference
    private AccountInfoApi accountInfoApi;

    //初始化生产者
    private static TransactionMQProducer producer;

    @PostConstruct
    public void initProduct() {
        //事务消息使用的生产者是TransactionMQProducer
        producer = new TransactionMQProducer(RocketConfig.GROUP1);
        producer.setSendMsgTimeout(10000);
        producer.setNamesrvAddr(RocketConfig.NAMESRVADDR);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新帐号余额‐发送消息
     * producer向MQ Server发送消息
     */
    public void sendUpdateAccountBalance(AccountChangeEvent ace) throws Exception {
        //构件消息体
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountChange", ace);

        //添加本地事务对应的监听
        producer.setTransactionListener(new TransactionListener() {
            //正常事务过程
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                try {
                    //解析消息内容
                    String jsonString = new String((byte[]) message.getBody());
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    AccountChangeEvent ace = JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
                    //扣除金额
                    boolean isOperationDataBase = doUpdateAccountBalance(ace);
                    if (isOperationDataBase) {
                        int i = 8 / 0;
                        return LocalTransactionState.COMMIT_MESSAGE;
                    } else {
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    }
                } catch (Exception e) {
                    System.out.println("事务执行失败");
                    e.printStackTrace();
                    return LocalTransactionState.UNKNOW;
                }
            }

            //事务补偿过程
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                LocalTransactionState state;
                try {
                    final JSONObject jsonObject = JSON.parseObject(new String(messageExt.getBody()));
                    AccountChangeEvent accountChangeEvent =
                            JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
                    //事务id
                    String txNo = accountChangeEvent.getTxNo();
                    int isexistTx = accountInfoApi.isExistTxA(txNo);
                    log.info("事务补偿，回查事务，事务号: {} 结果: {}", accountChangeEvent.getTxNo(), isexistTx);
                    if (isexistTx > 0) {
                        state = LocalTransactionState.COMMIT_MESSAGE;
                    } else {
                        state = LocalTransactionState.ROLLBACK_MESSAGE;
                    }
                } catch (Exception e) {
                    System.out.println("事务补偿执行失败");
                    e.printStackTrace();
                    return LocalTransactionState.UNKNOW;
                }
                return state;
            }
        });

        Message msg = new Message("accountTxTopic", jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        TransactionSendResult sendResult = producer.sendMessageInTransaction(msg, null);
        System.out.println("send transcation message body=" + msg.getBody() + ",result=" + sendResult.getSendStatus());
        //事务补偿过程必须保障服务器在运行过程中，否则将无法进行正常的事务补偿
        //producer.shutdown();
    }

    /**
     * 更新帐号余额‐本地事务
     * producer发送消息完成后接收到MQ Server的回应即开始执行本地事务
     * 返回是否真正执行了数据库减钱操作
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean doUpdateAccountBalance(AccountChangeEvent ace) {
        System.out.println("开始更新本地事务，事务号：" + ace.getTxNo());
        //查询账户余额是否够
        BigDecimal accountBalance = accountInfoApi.queryAccountBalance(ace.getAccountNoA());
        if (ObjectUtil.isEmpty(accountBalance)
                || accountBalance.subtract(ace.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        accountInfoApi.subtractAccountBalance(ace.getAccountNoA(), ace.getAmount());
        //为幂等性做准备
        accountInfoApi.addTxA(ace.getTxNo());
        //测试
        if (ace.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("bank1更新本地事务时抛出异常");
        }
        log.info("结束更新本地事务，事务号：{}", ace.getTxNo());
        return true;
    }
}