package com.adolesce.cloud.es.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.Page;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.adolesce.cloud.es.bean.EsQueryResult;
import com.adolesce.cloud.es.bean.EsResultOperator;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
     *
     * @param indexName        索引名称
     * @param mclass           字节码对象
     * @param queryBuilder     查询条件Builder
     * @param esResultOperator es查询结果操作
     *                         es深度分页问题：当查询分页深度较大时，汇总数据过多，对内存和CPU会产生非常大的压力，因此elasticsearch会禁止from+ size 超过10000的请求。
     *                         针对深度分页，ES提供了两种解决方案，[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html)：
     *                         - search after：分页时需要排序，原理是从上一次的排序值开始，查询下一页数据。官方推荐使用的方式。
     *                         - scroll：原理将排序后的文档id形成快照，保存在内存。官方已经不推荐使用。
     *                         <p>
     *                         分页查询的常见实现方案以及优缺点：
     *                         - `from + size`：
     *                         - 优点：支持随机翻页
     *                         - 缺点：深度分页问题，默认查询上限（from + size）是10000
     *                         - 场景：百度、京东、谷歌、淘宝这样的随机翻页搜索
     *                         <p>
     *                         - `after search`：
     *                         - 优点：没有查询上限（单次查询的size不超过10000）
     *                         - 缺点：只能向后逐页查询，不支持随机翻页
     *                         - 场景：没有随机翻页需求的搜索，例如手机向下滚动翻页
     *                         <p>
     *                         - `scroll`：
     *                         - 优点：没有查询上限（单次查询的size不超过10000）
     *                         - 缺点：会有额外内存消耗，并且搜索结果是非实时的
     */
    public EsQueryResult excuteQuery(String indexName, Class<?> mclass, QueryBuilder queryBuilder,
                                     EsResultOperator esResultOperator) throws Exception {
        //1、构建查询条件
        SearchRequest searchRequest = this.buildSearchRequest(indexName, queryBuilder, esResultOperator);
        //2、执行查询，获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //3、解析响应结果集
        return this.handleResponse(mclass, esResultOperator, searchResponse);
    }

    /**
     * 构建查询请求对象
     *
     * @param indexName      ES索引名称
     * @param queryBuilder   ES查询条件
     * @param resultOperator ES结果操作
     * @return
     */
    private SearchRequest buildSearchRequest(String indexName, QueryBuilder queryBuilder, EsResultOperator resultOperator) {
        //1、准备查询请求对象，指定查询的索引名称
        SearchRequest searchRequest = new SearchRequest(indexName);
        //2、准备查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        //3、指定查询条件(查询出所有记录总数，默认最多10000)
        sourceBulider.trackTotalHits(true);
        sourceBulider.query(queryBuilder);

        if (ObjectUtil.isNotEmpty(resultOperator)) {
            //4、指定普通排序（排序结果在结果数组中的前面）
            //5、指定分页
            Page page = resultOperator.getPage();
            if (ObjectUtil.isNotEmpty(page)) {
                Order[] orders = page.getOrders();
                if (ObjectUtil.isNotEmpty(orders)) {
                    Arrays.stream(orders).forEach(order ->
                            sourceBulider.sort(order.getField(), Direction.DESC.equals(order.getDirection()) ? SortOrder.DESC : SortOrder.ASC)
                    );
                }
                sourceBulider.from(Math.max((page.getPageNumber() - 1), 0) * page.getPageSize()).size(page.getPageSize());
            }
            //6、指定地理位置排序(某目标距指定某坐标距离进行排序)
            if (ObjectUtil.isNotEmpty(resultOperator.getGeoDistanceSortBuilder())) {
                sourceBulider.sort(resultOperator.getGeoDistanceSortBuilder());
            }
            //7、设置高亮
            if (ObjectUtil.isNotEmpty(resultOperator.getHighlightBuilder())) {
                sourceBulider.highlighter(resultOperator.getHighlightBuilder());
            }
        }

        //8、添加查询条件构造器
        searchRequest.source(sourceBulider);
        return searchRequest;
    }

    /**
     * 处理查询结果集
     *
     * @param mclass
     * @param esResultOperator
     * @param searchResponse
     * @return
     */
    private EsQueryResult handleResponse(Class mclass, EsResultOperator esResultOperator, SearchResponse searchResponse)
            throws NoSuchFieldException, IllegalAccessException {
        SearchHits searchHits = searchResponse.getHits();
        //1、获取记录数
        long total = searchHits.getTotalHits().value;
        System.err.println("总记录数：" + total);
        //2、获取结果集合
        SearchHit[] hits = searchHits.getHits();
        List<Object> objectList = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            //2.1、转为对象
            Object object = JSON.parseObject(json, mclass);
            //2.2、如果设置了按距离排序，设置距离信息（四舍五入保留两位小数）
            if (ObjectUtil.isNotEmpty(esResultOperator) && ObjectUtil.isNotEmpty(esResultOperator.getGeoDistanceSortBuilder())) {
                Object[] sortValues = hit.getSortValues();
                if (ArrayUtil.isNotEmpty(sortValues)) {
                    Field distance = mclass.getDeclaredField("distance");
                    if (ObjectUtil.isNotEmpty(distance)) {
                        distance.setAccessible(true);
                        distance.set(object, new BigDecimal((Double) sortValues[sortValues.length - 1]).setScale(2, RoundingMode.UP));
                    }
                }
            }
            //2.3、处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (CollUtil.isNotEmpty(map)) {
                // 2）根据字段名，获取高亮结果
                map.forEach((fieldName, highlightField) -> {
                    try {
                        Field field = mclass.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(object, highlightField.getFragments()[0].toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            objectList.add(object);
        }
        EsQueryResult queryResult = new EsQueryResult();
        queryResult.setTotal(total);
        queryResult.setResultList(objectList);
        return queryResult;
    }

    /**
     * 桶聚合时：根据聚合名称解析聚合结果
     *
     * @param aggregations 聚合结果集
     * @param aggName      聚合名称（聚合查询时自己起的聚合别名）
     * @return
     */
    public List<String> getAggResultForAggName(Aggregations aggregations, String aggName) {
        Terms agg = aggregations.get(aggName);
        List<? extends Terms.Bucket> buckets = agg.getBuckets();
        List<String> results = new ArrayList();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.err.println("key = " + key + ", docCount = " + docCount);
            results.add(key);
        }
        return results;
    }


    /**
     * 查询聚合结果
     *
     * @param indexName 索引名称
     * @param query     查询条件
     * @param aggs      聚合参数列表
     * @return Aggregations
     * @throws Exception
     */
    public Aggregations getAggregations(String indexName, QueryBuilder query, AggregationBuilder... aggs) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1、指定查询条件和文档结果条数0（聚合时无需得到文档结果）
        if (ObjectUtil.isNotNull(query)) {
            sourceBulider.query(query);
        }
        sourceBulider.size(0);

        // 2.、指定聚合参数
        Arrays.stream(aggs).forEach(agg -> sourceBulider.aggregation(agg));

        // 3、设置查询条件
        searchRequest.source(sourceBulider);

        // 4、发起查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();

        // 5、获取聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        //Map<String, Aggregation> aggregationMap = aggregations.asMap();
        return aggregations;
    }

    /**
     * 自动补全查询
     * @param indexName 索引名称
     * @param suggestName 自动补全名称
     * @param suggestField 自动补全字段名
     * @param prefix 自动补全前缀
     * @param suggestCount 自动补全词条数量
     * @return 自动补全结果
     *
     * GET /indexName/_search
     * {
     *  "suggest": {
     *   "suggestName": {
     *    "text": "s", // 关键字
     *    "completion": {
     *     "field": "suggestField", // 补全查询的字段
     *     "skip_duplicates": true, // 跳过重复的
     *     "size": 10 // 获取前10条结果
     *    }
     *   }
     *  }
     * }
     *
     */
    public List<String> suggestionQuery(String indexName, String suggestName, String suggestField, String prefix,Integer suggestCount) throws IOException {
        // 1.准备请求
        SearchRequest request = new SearchRequest(indexName);
        // 2.请求参数
        request.source().suggest(new SuggestBuilder()
                .addSuggestion(
                        suggestName,
                        SuggestBuilders
                                .completionSuggestion(suggestField)
                                .size(suggestCount)
                                .skipDuplicates(true)
                                .prefix(prefix)
                ));
        // 3.发出请求
        SearchResponse response = this.client.search(request, RequestOptions.DEFAULT);
        // 4.解析
        Suggest suggest = response.getSuggest();
        // 4.1.根据名称获取结果
        CompletionSuggestion suggestion = suggest.getSuggestion(suggestName);
        // 4.2.获取options
        List<String> resultList = new ArrayList<>();
        for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
            // 4.3.获取补全的结果
            String str = option.getText().toString();
            // 4.4.放入集合
            resultList.add(str);
        }
        return resultList;
    }
}