package com.adolesce.cloud.dubbo.domain.es;

import cn.hutool.core.bean.BeanUtil;
import com.adolesce.cloud.dubbo.domain.db.EsHotel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class HotelDoc implements Serializable {
    private Long id;    //id
    private String name;  //酒店名称
    private String address; //酒店地址
    private Integer price;  //单晚价格
    private Integer score;  //评分
    private String brand;   //酒店品牌
    private String city;    //城市
    private String starName;    //星级
    private String business;    //商圈
    private String location;    //经纬度地理位置
    private String pic;         //酒店形象图片
    private Object distance;
    private Boolean isAD;
    private List<String> suggestion; //自动补全字段

    public HotelDoc(EsHotel hotel) {
        BeanUtil.copyProperties(hotel, this);
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        // 组装suggestion
        if (this.business.contains("/")) {
            // business有多个值，需要切割
            String[] arr = this.business.split("/");
            // 添加元素
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.brand);
            Collections.addAll(this.suggestion, arr);
        }else if (this.business.contains("、")) {
            // business有多个值，需要切割
            String[] arr = this.business.split("、");
            // 添加元素
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.brand);
            Collections.addAll(this.suggestion, arr);
        } else {
            this.suggestion = Arrays.asList(this.brand, this.business);
        }
    }
}
