package com.adolesce.mqconsumer.service;

import com.adolesce.cloud.dubbo.api.db.AccountInfoApi;
import com.adolesce.common.vo.AccountChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountInfoService {
    //@DubboReference
    private AccountInfoApi accountInfoApi;

    /**
     * 消费消息，更新本地事务，添加金额
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAccountInfoBalance(AccountChangeEvent ace) {
        log.info("bank2更新本地账号，账号：{},金额： {}", ace.getAccountNoB(), ace.getAmount());
        int tx_no = accountInfoApi.isExistTxB(ace.getTxNo());
        if (tx_no <= 0) {
            accountInfoApi.addAccountBalance(ace.getAccountNoB(), ace.getAmount());
            accountInfoApi.addTxB(ace.getTxNo());
            log.info("更新本地事务执行成功，本次事务号: {}", ace.getTxNo());
        } else {
            log.info("更新本地事务执行失败，本次事务号: {}", ace.getTxNo());
        }
        //测试
        /*if (ace.getAmount().compareTo(BigDecimal.ONE) == 0) {
            throw new RuntimeException("bank2更新本地事务时抛出异常");
        }*/
    }
}