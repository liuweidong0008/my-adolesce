package com.adolesce.cloud.dubbo.domain.db;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 商品实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("goods")
public class MyGoods {
    private Integer id;
    private String title; //商品标题
    private double price; //商品价格
    private int stock; //商品库存
    @TableField("saleNum")
    private int saleNum;
    @TableField("createTime")
    private Date createTime;
    @TableField("categoryName")
    private String categoryName;
    @TableField("brandName")
    private String brandName;
    @TableField(exist = false)
    private Map spec;

    @JSONField(serialize = false)//在转换JSON时，忽略该字段
    @TableField("spec")
    private String specStr;//接收数据库的信息 "{"":"","":""}"   {"":"","":""}
}
