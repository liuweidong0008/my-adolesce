package com.adolesce.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangeEvent implements Serializable {
    /**
     * 账号(减钱的账户)
     */
    private String accountNoA;
    /**
     * 账号(加钱的账户)
     */
    private String accountNoB;
    /**
     * 变动金额
     */
    private BigDecimal amount;
    /**
     * 事务号
     */
    private String txNo;
}
