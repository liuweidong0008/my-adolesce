package com.adolesce.common.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

public interface AccountInfoMapper {
    @Update("update account_info set account_balance = account_balance - #{amount} where account_no = #{accountNo}")
    int subtractAccountBalance(@Param("accountNo") String accountNo, @Param("amount") BigDecimal amount);

    @Update("update account_info set account_balance = account_balance + #{amount} where account_no = #{accountNo}")
    int addAccountBalance(@Param("accountNo") String accountNo, @Param("amount") BigDecimal amount);

    @Select("select account_balance from account_info where account_no = #{accountNo}")
    BigDecimal queryAccountBalance(@Param("accountNo") String accountNo);

    @Select("select count(1) from transaction_info_a where tx_no = #{txNo}")
    int isExistTxA(String txNo);

    @Insert("insert into transaction_info_a values(#{txNo},now());")
    int addTxA(String txNo);


    @Select("select count(1) from transaction_info_b where tx_no = #{txNo}")
    int isExistTxB(String txNo);

    @Insert("insert into transaction_info_b values(#{txNo},now());")
    int addTxB(String txNo);
}