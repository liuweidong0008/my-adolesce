package com.adolesce.common.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

public interface AccountInfoMapper {
    /**
     * 账户扣款
     * @param accountNo 被扣款账号
     * @param amount 扣款金额
     * @return 更新影响行数
     */
    @Update("update account_info set account_balance = account_balance - #{amount} where account_no = #{accountNo}")
    int subtractAccountBalance(@Param("accountNo") String accountNo, @Param("amount") BigDecimal amount);

    /**
     * 账户转账
     * @param accountNo 被转账账号
     * @param amount 转账金额
     * @return 更新影响行数
     */
    @Update("update account_info set account_balance = account_balance + #{amount} where account_no = #{accountNo}")
    int addAccountBalance(@Param("accountNo") String accountNo, @Param("amount") BigDecimal amount);

    /**
     * 查询账户余额
     * @param accountNo 账号
     * @return 账户余额
     */
    @Select("select account_balance from account_info where account_no = #{accountNo}")
    BigDecimal queryAccountBalance(@Param("accountNo") String accountNo);

    /**
     * 查询该事务号在A库是否已经存在
     * @param txNo 事务号
     * @return 0：否 1：是
     */
    @Select("select count(1) from transaction_info_a where tx_no = #{txNo}")
    int isExistTxA(String txNo);

    /**
     * A库添加事务记录
     * @param txNo
     * @return
     */
    @Insert("insert into transaction_info_a values(#{txNo},now());")
    int addTxA(String txNo);

    /**
     * 查询该事务号在B库是否已经存在
     * @param txNo 事务号
     * @return 0：否 1：是
     */
    @Select("select count(1) from transaction_info_b where tx_no = #{txNo}")
    int isExistTxB(String txNo);

    /**
     * B库添加事务记录
     * @param txNo
     * @return
     */
    @Insert("insert into transaction_info_b values(#{txNo},now());")
    int addTxB(String txNo);
}