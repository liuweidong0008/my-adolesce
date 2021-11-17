package com.adolesce.cloud.dubbo.api.db;


import java.math.BigDecimal;

public interface AccountInfoApi {

    int isExistTxA(String txNo);

    BigDecimal queryAccountBalance(String accountNoA);

    void subtractAccountBalance(String accountNoA, BigDecimal amount);

    void addTxA(String txNo);

    int isExistTxB(String txNo);

    void addAccountBalance(String accountNoB, BigDecimal amount);

    void addTxB(String txNo);
}
