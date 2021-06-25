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
 * 测试rocketmq分布式事务消息
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