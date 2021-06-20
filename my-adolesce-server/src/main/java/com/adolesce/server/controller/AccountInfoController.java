package com.adolesce.server.controller;

import com.adolesce.common.mapper.AccountInfoMapper;
import com.adolesce.common.vo.AccountChangeEvent;
import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("account")
public class AccountInfoController {
    @Autowired
    private AccountInfoMapper accountInfoMapper;
    @Autowired
    private AccountInfoService accountInfoService;
    //http://localhost:8081/my_adolesce/account/transfer?accountNoA=629098789670876&accountNoB=629098789690909&amount=100
    @GetMapping("/transfer")
    public Response transfer(String accountNoA,String accountNoB, BigDecimal amount) throws Exception {
        String tx_no= UUID.randomUUID().toString();
        AccountChangeEvent ace = new AccountChangeEvent(accountNoA,accountNoB,amount,tx_no);
        accountInfoService.sendUpdateAccountBalance(ace);
        return Response.success();
    }
}