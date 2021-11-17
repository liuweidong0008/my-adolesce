package com.adolesce.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    private int id;
    private String title;//商品标题
    private double price;//商品价格
    private int count;//商品库存
}
