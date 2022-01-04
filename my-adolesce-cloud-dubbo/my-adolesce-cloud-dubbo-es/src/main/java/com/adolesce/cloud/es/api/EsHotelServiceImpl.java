package com.adolesce.cloud.es.api;

import cn.hutool.db.Page;
import com.adolesce.cloud.dubbo.api.es.EsSearchHotelApi;
import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;
import com.adolesce.cloud.dubbo.domain.es.HotelDoc;
import com.adolesce.cloud.es.config.EsSearchCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService
public class EsHotelServiceImpl implements EsSearchHotelApi {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private EsSearchCommonService esSearchCommonService;

    @Override
    public EsPageResult search(EsRequestParams params) {
        try {
            // 1.准备请求参数
            // 1.1.query
            AbstractQueryBuilder queryBuilder = buildBasicQuery(params);
            // 1.2.分页
            Page page = new Page(params.getPage(),params.getSize());
            // 1.3.距离排序
            String location = params.getLocation();
            GeoDistanceSortBuilder distanceSortBuilder = null;
            if (StringUtils.isNotBlank(location)) {
                distanceSortBuilder = SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS);
            }
            Map<String, Object> resultMap = esSearchCommonService.excuteQuery("hotel", HotelDoc.class, queryBuilder, page, null, distanceSortBuilder);
            List<HotelDoc> results = (List<HotelDoc>) resultMap.get("result");
            Long total = (Long) resultMap.get("total");
            return new EsPageResult(total, results);
        } catch (IOException e) {
            throw new RuntimeException("搜索数据失败", e);
        }
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


    /*public EsPageResult oldSearch(EsRequestParams params) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("hotel");
            // 2.准备请求参数
            // 2.1.query
            request.source().query(buildBasicQuery(params, request));
            // 2.2.分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);
            // 2.3.距离排序
            String location = params.getLocation();
            if (StringUtils.isNotBlank(location)) {
                request.source().sort(SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS)
                );
            }
            // 3.发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("搜索数据失败", e);
        }
    }

    private EsPageResult handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (map != null && !map.isEmpty()) {
                // 2）根据字段名，获取高亮结果
                HighlightField highlightField = map.get("name");
                if (highlightField != null) {
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightField.getFragments()[0].toString();
                    // 4）把高亮结果放到HotelDoc中
                    hotelDoc.setName(hName);
                }
            }
            // 4.8.排序信息
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                hotelDoc.setDistance(sortValues[0]);
            }

            // 4.9.放入集合
            hotels.add(hotelDoc);
        }
        return new EsPageResult(total, hotels);
    }*/
}
