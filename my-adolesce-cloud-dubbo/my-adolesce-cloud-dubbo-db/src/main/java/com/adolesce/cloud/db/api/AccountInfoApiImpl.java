package com.adolesce.cloud.db.api;

import com.adolesce.cloud.db.mapper.AccountInfoMapper;
import com.adolesce.cloud.dubbo.api.db.AccountInfoApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@DubboService
public class AccountInfoApiImpl implements AccountInfoApi {

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    /**
     * 查询该事务号在A库是否已经存在
     *
     * @param txNo 事务号
     * @return 0：否 1：是
     */
    @Override
    public int isExistTxA(String txNo) {
        return accountInfoMapper.isExistTxA(txNo);
    }

    /**
     * 查询账户余额
     *
     * @param accountNo 账号
     * @return 账户余额
     */
    @Override
    public BigDecimal queryAccountBalance(String accountNo) {
        return accountInfoMapper.queryAccountBalance(accountNo);
    }

    /**
     * 账户扣款
     *
     * @param accountNo 被扣款账号
     * @param amount    扣款金额
     * @return 更新影响行数
     */
    @Override
    public void subtractAccountBalance(String accountNo, BigDecimal amount) {
        accountInfoMapper.subtractAccountBalance(accountNo,amount);
    }

    /**
     * A库添加事务记录
     *
     * @param txNo
     * @return
     */
    @Override
    public void addTxA(String txNo) {
        accountInfoMapper.addTxA(txNo);
    }

    /**
     * 查询该事务号在B库是否已经存在
     *
     * @param txNo 事务号
     * @return 0：否 1：是
     */
    @Override
    public int isExistTxB(String txNo) {
        return accountInfoMapper.isExistTxB(txNo);
    }

    /**
     * 账户转账
     *
     * @param accountNo 被转账账号
     * @param amount    转账金额
     * @return 更新影响行数
     */
    @Override
    public void addAccountBalance(String accountNo, BigDecimal amount) {
        accountInfoMapper.addAccountBalance(accountNo,amount);
    }

    /**
     * B库添加事务记录
     *
     * @param txNo
     * @return
     */
    @Override
    public void addTxB(String txNo) {
        accountInfoMapper.addTxB(txNo);
    }
}
