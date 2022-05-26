package com.adolesce.common.entity;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/3/15 12:03
 */
@Data
public class Order {
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 订单商品
     */
    private Goods goods;
}
