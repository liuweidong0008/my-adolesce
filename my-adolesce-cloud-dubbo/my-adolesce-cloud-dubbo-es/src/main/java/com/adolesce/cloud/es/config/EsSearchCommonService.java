package com.adolesce.cloud.es.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.Page;
import cn.hutool.db.sql.Direction;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/1 22:36
 */
@Component
public class EsSearchCommonService {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 执行es search查询，获取结果可参考脚本执行后的json结构
     * @param index    索引名称
     * @param mclass   字节码对象
     * @param queryBuilder    查询条件Builder
     * @param page 分页排序对象
     * @param hlBuilder 高亮构造条件Builder
     * @param distanceSortBuilder 地理位置排序Builder
     *
     * es深度分页问题：当查询分页深度较大时，汇总数据过多，对内存和CPU会产生非常大的压力，因此elasticsearch会禁止from+ size 超过10000的请求。
     * 针对深度分页，ES提供了两种解决方案，[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html)：
     *      - search after：分页时需要排序，原理是从上一次的排序值开始，查询下一页数据。官方推荐使用的方式。
     *      - scroll：原理将排序后的文档id形成快照，保存在内存。官方已经不推荐使用。
     *
     * 分页查询的常见实现方案以及优缺点：
     * - `from + size`：
     *   - 优点：支持随机翻页
     *   - 缺点：深度分页问题，默认查询上限（from + size）是10000
     *   - 场景：百度、京东、谷歌、淘宝这样的随机翻页搜索
     *
     * - `after search`：
     *   - 优点：没有查询上限（单次查询的size不超过10000）
     *   - 缺点：只能向后逐页查询，不支持随机翻页
     *   - 场景：没有随机翻页需求的搜索，例如手机向下滚动翻页
     *
     * - `scroll`：
     *   - 优点：没有查询上限（单次查询的size不超过10000）
     *   - 缺点：会有额外内存消耗，并且搜索结果是非实时的
     */
     public Map<String,Object> excuteQuery(String index, Class<?> mclass, QueryBuilder queryBuilder, Page page,
                                HighlightBuilder hlBuilder, GeoDistanceSortBuilder distanceSortBuilder) throws Exception {
        //1、构建查询条件
        SearchRequest searchRequest = this.getSearchRequest(index, queryBuilder, page, hlBuilder, distanceSortBuilder);
        //2、执行查询，获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //3、解析响应结果集
        return this.handleResponse(mclass, distanceSortBuilder, searchResponse);
    }

    /**
     * 处理结果集
     * @param mclass
     * @param distanceSortBuilder
     * @param searchResponse
     * @return
     */
    private Map<String,Object> handleResponse(Class<?> mclass, GeoDistanceSortBuilder distanceSortBuilder, SearchResponse searchResponse) throws NoSuchFieldException, IllegalAccessException {
        Map<String,Object> responseResult = new HashMap<>();
        SearchHits searchHits = searchResponse.getHits();
        //1、获取记录数
        long total = searchHits.getTotalHits().value;
        responseResult.put("total",total);
        System.err.println("总记录数：" + total);
        //2、获取结果集合
        SearchHit[] hits = searchHits.getHits();
        List objectList = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            //2.1、转为对象
            Object object = JSON.parseObject(json, mclass);
            //2.2、如果设置了按距离排序，设置距离信息（四舍五入保留两位小数）
            if(ObjectUtil.isNotEmpty(distanceSortBuilder)){
                Object[] sortValues = hit.getSortValues();
                if (ArrayUtil.isNotEmpty(sortValues)) {
                    Field distance = mclass.getDeclaredField("distance");
                    if(ObjectUtil.isNotEmpty(distance)){
                        distance.setAccessible(true);
                        distance.set(object,new BigDecimal((Double)sortValues[sortValues.length - 1]).setScale(2, RoundingMode.UP));
                    }
                }
            }
            //2.3、处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (CollUtil.isNotEmpty(map)) {
                // 2）根据字段名，获取高亮结果
                map.forEach((fieldName,highlightField) -> {
                    try {
                        Field field = mclass.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(object,highlightField.getFragments()[0].toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            objectList.add(object);
        }
        responseResult.put("result",objectList);
        return responseResult;
    }

    /**
     * 构建查询请求对象
     * @param index
     * @param queryBuilder
     * @param page
     * @param hlBuilder
     * @param distanceSortBuilder
     * @return
     */
    private SearchRequest getSearchRequest(String index, QueryBuilder queryBuilder, Page page, HighlightBuilder hlBuilder, GeoDistanceSortBuilder distanceSortBuilder) {
        //1、准备查询请求对象，指定查询的索引名称
        SearchRequest searchRequest = new SearchRequest(index);
        //2、准备查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();
        //3.1、指定查询条件(查询出所有记录总数，默认最多10000)
        sourceBulider.trackTotalHits(true);
        sourceBulider.query(queryBuilder);
        if(ObjectUtil.isNotEmpty(page)){
            //3.2、指定普通排序（排序结果在结果数组中的第一个）
            if (!StringUtils.isEmpty(page.getOrders())) {
                Arrays.stream(page.getOrders()).forEach(order ->
                        sourceBulider.sort(order.getField(), Direction.DESC.equals(order.getDirection()) ? SortOrder.DESC:SortOrder.ASC)
                );
            }
            //3.3、指定分页
            sourceBulider.from(Math.max((page.getPageNumber() - 1) , 0) * page.getPageSize()).size(page.getPageSize());
        }
        //3.4、指定地理位置排序(以当前查询条件中的坐标为距离计算目标点)
        if(ObjectUtil.isNotEmpty(distanceSortBuilder)){
            sourceBulider.sort(distanceSortBuilder);
        }
        //3.5、设置高亮
        if(ObjectUtil.isNotEmpty(hlBuilder)){
            sourceBulider.highlighter(hlBuilder);
        }
        //4、添加查询条件构造器
        searchRequest.source(sourceBulider);
        return searchRequest;
    }
}