package com.adolesce.server.controller;

import com.adolesce.common.vo.AccountChangeEvent;
import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 测试rocketmq分布式事务消息演示类（生产者）
 * 1、生产者发送half半消息至broker，此时broker接收后，并不会将该消息推给consumer消费者
 * 2、broker接收成功后会发送OK回执给生产者，此时会调用executeLocalTransaction方法去执行本地事务
 *      1）、如果本地事务执行成功，返回COMMIT_MESSAGE，告诉broker可以把half消息变为普通消息，此时消费者可接收到该消息并正常消费了
 *      2）、如果本地事务执行异常，返回ROLLBACK_MESSAGE，broker会把half消息进行删除
 *      3）、如果系统异常，未返回给broker回执，或者返回UNKNOW，此时每隔一定时间会自动回调checkLocalTransaction进行事务补偿
 *
 * http://localhost:8081/my_adolesce/account/transfer?accountNoA=629098789670876&accountNoB=629098789690909&amount=100
 */

@RestController
@RequestMapping("account")
public class AccountInfoController {
    @Autowired
    private AccountInfoService accountInfoService;

    /**
     *
     * @param accountNoA 账户A（转钱账户）
     * @param accountNoB 账户B（收钱账户）
     * @param amount （转账金额）
     * @return
     * @throws Exception
     */
    @GetMapping("/transfer")
    public Response transfer(String accountNoA,String accountNoB, BigDecimal amount) throws Exception {
        //分布式事务号
        String tx_no= UUID.randomUUID().toString();
        //账户改变事件（消息）
        AccountChangeEvent ace = new AccountChangeEvent(accountNoA,accountNoB,amount,tx_no);
        accountInfoService.sendUpdateAccountBalance(ace);
        return Response.success();
    }
}