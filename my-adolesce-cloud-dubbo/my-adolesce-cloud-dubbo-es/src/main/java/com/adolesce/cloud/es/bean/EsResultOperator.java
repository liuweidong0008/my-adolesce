package com.adolesce.cloud.es.bean;

import cn.hutool.db.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;

/**
 * es搜索结果集处理对象封装
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/6/14 19:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsResultOperator {
    /**
     * 分页排序对象 : 普通排序、分页封装
     */
    private Page page;

    /**
     * 地理位置排序Builder : 特殊地理位置排序（以指定中心点进行距离排序）
     */
    private GeoDistanceSortBuilder geoDistanceSortBuilder;

    /**
     * 高亮构造条件Builder :指定高亮结果处理
     */
    private HighlightBuilder highlightBuilder;

    public static EsResultOperator build(){
        return new EsResultOperator();
    }

    public EsResultOperator page(Page page){
        this.setPage(page);
        return this;
    }

    public EsResultOperator geoSort(GeoDistanceSortBuilder builder){
        this.setGeoDistanceSortBuilder(builder);
        return this;
    }

    public EsResultOperator highLight(HighlightBuilder builder){
        this.setHighlightBuilder(builder);
        return this;
    }
}
