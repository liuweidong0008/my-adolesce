package com.adolesce.consul.provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品实体类
 */
@Data
@AllArgsConstructor
public class Goods {
    private int id;
    private String title;//商品标题
    private double price;//商品价格
    private int count;//商品库存
}
