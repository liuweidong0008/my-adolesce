package com.adolesce.cloud.es.api;

import cn.hutool.db.Page;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.adolesce.cloud.dubbo.api.es.EsSearchHotelApi;
import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;
import com.adolesce.cloud.dubbo.domain.es.HotelDoc;
import com.adolesce.cloud.es.bean.EsQueryResult;
import com.adolesce.cloud.es.bean.EsResultOperator;
import com.adolesce.cloud.es.service.EsSearchCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService
@RefreshScope
public class EsHotelServiceImpl implements EsSearchHotelApi {
    @Autowired
    private EsSearchCommonService esSearchCommonService;
    @Value("${elasticsearch.suggestionCount}")
    private Integer suggestionCount;
    @Value("${elasticsearch.aggregationCount}")
    private Integer aggregationCount;

    /**
     * 条件查询
     * @param params 查询参数
     * @return
     */
    @Override
    public EsPageResult search(EsRequestParams params)  {
        try {
            // 1.准备请求参数
            // 1.1、构建查询条件
            QueryBuilder queryBuilder = buildBasicQuery(params);
            // 1.2、构建分页参数
            Page page = new Page(params.getPage(),params.getSize());
            // 1.3、构建以某个点为中心排序 Builder
            String location = params.getLocation();
            GeoDistanceSortBuilder distanceSortBuilder = null;
            if (StringUtils.isNotBlank(location)) {
                distanceSortBuilder = SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS);
            }
            //1.4、构建排序
            String sortBy = params.getSortBy();
            if(StringUtils.isNotBlank(sortBy) && !StringUtils.equals("default",sortBy)){
                page.setOrder(new Order(sortBy, "price".equals(sortBy)?Direction.ASC:Direction.DESC));
            }

            //1.5、构建高亮 Builder
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                        .field("name").requireFieldMatch(false)
                        .field("address").requireFieldMatch(false)   //不会参与高亮，因为该字段映射中未参与搜索
                        .field("business").requireFieldMatch(false); //会参与高亮，但是由于该字段是keyword类型，因此必须跟分词后的某个词条完全匹配才会高亮

            //2、执行查询，解析结果
            EsQueryResult queryResult = esSearchCommonService.excuteQuery("hotel", HotelDoc.class, queryBuilder,
                    EsResultOperator.build().page(page).highLight(highlightBuilder).geoSort(distanceSortBuilder));
            List<HotelDoc> results = (List<HotelDoc>) queryResult.getResultList();
            return new EsPageResult(queryResult.getTotal(), results);
        } catch (Exception e) {
            throw new RuntimeException("搜索数据失败", e);
        }
    }

    /**
     * 聚合查询
     * @param params 查询条件
     * @return
     */
    @Override
    public Map<String, List<String>> getFilters(EsRequestParams params) {
        try {
            // 1、构建查询条件
            QueryBuilder queryBuilder = buildBasicQuery(params);
            // 2、构建桶聚合
            TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg").field("brand").size(aggregationCount);
            TermsAggregationBuilder cityAgg = AggregationBuilders.terms("cityAgg").field("city").size(aggregationCount);
            TermsAggregationBuilder starAgg = AggregationBuilders.terms("starAgg").field("starName").size(aggregationCount);

            Aggregations aggregations = this.esSearchCommonService.getAggregations("hotel",queryBuilder,brandAgg,cityAgg,starAgg);

            Map<String, List<String>> filters = new HashMap<>(3);
            // 4.1.解析品牌
            List<String> brandList = this.esSearchCommonService.getAggResultForAggName(aggregations, "brandAgg");
            filters.put("brand", brandList);
            // 4.1.解析城市
            List<String> cityList = this.esSearchCommonService.getAggResultForAggName(aggregations, "cityAgg");
            filters.put("city", cityList);
            // 4.1.解析星级
            List<String> starList = this.esSearchCommonService.getAggResultForAggName(aggregations, "starAgg");
            filters.put("starName", starList);

            return filters;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自动补全查询
     * @param key 补全前缀
     * @return
     */
    @Override
    public List<String> getSuggestion(String key) {
        List<String> resultList;
        try {
            resultList = this.esSearchCommonService.suggestionQuery("hotel", "mySuggest", "suggestion",key,suggestionCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }

    private AbstractQueryBuilder buildBasicQuery(EsRequestParams params) {
        // 1.准备Boolean查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 1.1.关键字搜索，match查询，放到must中
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            // 不为空，根据关键字查询
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            // 为空，查询所有
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        // 1.2.品牌
        String brand = params.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 1.3.城市
        String city = params.getCity();
        if (StringUtils.isNotBlank(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }
        // 1.4.星级
        String starName = params.getStarName();
        if (StringUtils.isNotBlank(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }
        // 1.5.价格范围
        Integer minPrice = params.getMinPrice();
        Integer maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            maxPrice = maxPrice == 0 ? Integer.MAX_VALUE : maxPrice;
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

        // 2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery, // 原始查询，boolQuery
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10) // 算分函数
                        )
                }
        );
        // 3.返回查询条件
        return functionScoreQuery;
    }
}
