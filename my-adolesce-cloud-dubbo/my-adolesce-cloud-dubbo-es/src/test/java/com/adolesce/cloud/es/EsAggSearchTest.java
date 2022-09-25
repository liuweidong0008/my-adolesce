package com.adolesce.cloud.es;

import com.adolesce.cloud.es.service.EsSearchCommonService;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.ParsedStats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/6/16 23:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsAggSearchTest {
    @Autowired
    private EsSearchCommonService esSearchCommonService;

    /**
     * 一、桶聚合查询 :相当于MySQL的 group by 操作。不要对text类型的数据进行分组，会失败
     * 1. 查询title包含手机的数据
     * 2. 查询品牌列表
     *
     * 二、指标聚合查询 :相当于MySQL的聚合函数。max、min、avg、sum等
     * 1. 查询title包含手机的数据
     * 2. 查询价格最高的
     *
     * GET /goods/_search
     * {
     *   "query": {
     *     "match": {
     *       "title": "手机"
     *     }
     *   },
     *   "size": 0,
     *   "aggs": {
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brandName",
     *         "size": 20
     *       }
     *     },
     *     "maxPriceAgg":{
     *       "max": {
     *         "field": "price"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testBucketAggQuery1() throws Exception {
        //1、构建查询条件：根据手机匹配titile字段
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");

        //2.1 创建统计Builder对象，根据brandName分组，查询brandName列表，起个别名叫：brandAgg
        AggregationBuilder agg1 = AggregationBuilders.terms("brandAgg").field("brandName").size(20);
        //2.2  创建统计Builder对象，查询price最大值，起个别名叫：maxPriceAgg
        AggregationBuilder agg2 = AggregationBuilders.max("maxPriceAgg").field("price");

        //3、执行聚合查询，返回聚合结果
        Aggregations aggregations = this.esSearchCommonService.getAggregations("goods", query, agg1, agg2);

        //4、获取、遍历桶聚合结果
        List<String> brandList = this.esSearchCommonService.getAggResultForAggName(aggregations,"brandAgg");
        brandList.stream().forEach(System.err::println);

        //5、获取指标聚合结果
        ParsedMax maxPriceAgg = aggregations.get("maxPriceAgg");
        double maxPrice = maxPriceAgg.getValue();
        System.err.println(maxPrice);
    }

    /**
     * GET /hotel/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brand",
     *         "size": 20,
     *         "order": {
     *           "_count": "asc"
     *         }
     *       }
     *     },
     *     "cityAgg":{
     *       "terms": {
     *         "field": "city",
     *         "size": 20
     *       }
     *     },
     *     "starNameAgg":{
     *        "terms": {
     *         "field": "starName",
     *         "size": 20
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testBucketAggQuery2() throws Exception {
        //1 创建统计Builder对象，根据brandName分组，查询brandName列表，起个别名叫：brandAgg
        AggregationBuilder agg1 = AggregationBuilders.terms("brandAgg").field("brand").size(20).order(BucketOrder.count(true));
        AggregationBuilder agg2 = AggregationBuilders.terms("cityAgg").field("city").size(20);
        AggregationBuilder agg3 = AggregationBuilders.terms("starNameAgg").field("starName").size(20);

        //2、执行聚合查询，返回聚合结果
        Aggregations aggregations = this.esSearchCommonService.getAggregations("hotel", null, agg1,agg2,agg3);

        //3、获取、遍历桶聚合结果
        List<String> brandList = this.esSearchCommonService.getAggResultForAggName(aggregations,"brandAgg");
        brandList.stream().forEach(System.err::println);

        System.out.println("---------------------------");

        List<String> cityList = this.esSearchCommonService.getAggResultForAggName(aggregations,"cityAgg");
        cityList.stream().forEach(System.err::println);

        System.out.println("---------------------------");

        List<String> starNameList = this.esSearchCommonService.getAggResultForAggName(aggregations,"starNameAgg");
        starNameList.stream().forEach(System.err::println);
    }

    /**
     * GET /hotel/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brand",
     *         "size": 20,
     *         "order": {
     *           "scoreAgg.avg": "desc"
     *         }
     *       },
     *       "aggs": {
     *         "scoreAgg": {
     *           "stats": {
     *             "field": "score"
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testBucketAggQuery3() throws Exception {
        //1 创建统计Builder对象，根据brandName分组，查询brandName列表，起个别名叫：brandAgg
        AggregationBuilder agg1 = AggregationBuilders.terms("brandAgg").field("brand").size(20).
                order(BucketOrder.aggregation("scoreAgg.avg",false)).
                //order(BucketOrder.aggregation("scoreAgg","avg",false)).
                subAggregation(AggregationBuilders.stats("scoreAgg").field("score"));

        //2、执行聚合查询，返回聚合结果
        Aggregations aggregations = this.esSearchCommonService.getAggregations("hotel", null, agg1);

        //3、获取、遍历桶聚合结果
        Terms agg = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> buckets = agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.err.println("key = " + key + ", docCount = " + docCount);

            ParsedStats scoreAggregation = bucket.getAggregations().get("scoreAgg");
            double avg = scoreAggregation.getAvg();
            System.err.println("key = " + key + ", avg = " + avg);
        }
    }

    /**
     * GET /hotel/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brand",
     *         "size": 20,
     *         "order": {
     *           "scoreAvgAgg": "desc"
     *         }
     *       },
     *       "aggs": {
     *         "scoreAvgAgg": {
     *           "avg": {
     *             "field": "score"
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testBucketAggQuery4() throws Exception {
        //1 创建统计Builder对象，根据brandName分组，查询brandName列表，起个别名叫：brandAgg
        AggregationBuilder agg1 = AggregationBuilders.terms("brandAgg").field("brand").size(20).
                order(BucketOrder.aggregation("scoreAvgAgg",false)).
                //order(BucketOrder.aggregation("scoreAvgAgg.value",false)).
                //order(BucketOrder.aggregation("scoreAvgAgg","value",false)).
                subAggregation(AggregationBuilders.avg("scoreAvgAgg").field("score"));

        //2、执行聚合查询，返回聚合结果
        Aggregations aggregations = this.esSearchCommonService.getAggregations("hotel", null, agg1);

        //3、获取、遍历桶聚合结果
        Terms agg = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> buckets = agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.err.println("key = " + key + ", docCount = " + docCount);

            ParsedAvg scoreAvgAggregation = bucket.getAggregations().get("scoreAvgAgg");
            double avg = scoreAvgAggregation.getValue();
            System.err.println("key = " + key + ", avg = " + avg);
        }
    }

}
