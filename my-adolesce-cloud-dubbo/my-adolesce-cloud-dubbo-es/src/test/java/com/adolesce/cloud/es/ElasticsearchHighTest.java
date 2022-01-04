package com.adolesce.cloud.es;

import cn.hutool.db.Page;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.adolesce.cloud.dubbo.domain.db.ESGoods;
import com.adolesce.cloud.dubbo.domain.es.HotelDoc;
import com.adolesce.cloud.es.config.EsSearchCommonService;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ES 高级操作
 *  1、各种查询
 *  2、索引别名和重建索引
 *
 * 常见的查询类型包括：
 *  1-查询所有：查询出所有数据，一般测试用。例如：match_all
 *  2-全文检索（full text）查询：利用分词器对用户输入内容分词，然后去倒排索引库中匹配。例如：
 *      - match_query
 *      - multi_match_query
 *  3-精确查询：根据精确词条值查找数据，一般是查找keyword、数值、日期、boolean等类型字段。例如：
 *      - ids
 *      - range
 *      - term
 *  4- 地理（geo）查询：根据经纬度查询。例如：
 *      - geo_distance
 *      - geo_bounding_box
 *  5- 复合（compound）查询：复合查询可以将上述各种查询条件组合起来，合并查询条件。例如：
 *      - bool
 *      - function_score
 *  所有的查询的语法基本一致:
 *     GET /indexName/_search
 *     {
 *       "query": {
 *         "查询类型": {
 *           "查询条件": "条件值"
 *         }
 *       }
 *     }
 */
@SpringBootTest
class ElasticsearchHighTest {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ElasticsearchRestTemplate esTemplate;
    @Autowired
    private EsSearchCommonService esSearchCommonService;

    /**
     * 查询所有：matchAll
     * 分页。默认显示10条
     *
     * DSL：
     *      GET /indexName/_search
     *      {
     *        "query": {
     *          "match_all": {}
     *        }
     *      }
     */
    @Test
    public void testMatchAll1() throws IOException {
        //查询所有商品文档
        QueryBuilder query = QueryBuilders.matchAllQuery();

        //执行查询
        excuteGoodsQuery(query, null,null,null);
    }
    @Test
    public void testMatchAll2() throws IOException {
        //查询所有酒店文档
        QueryBuilder query = QueryBuilders.matchAllQuery();
        //执行查询
        excuteHotelQuery(query, null,null,null);
    }


    /**
     * 全文检索-单字段查询：match（对传入的查询条件先进行分词，然后根据每个分词进行词条的等值匹配）
     * 默认为OR查询：求并集，可修改为AND查询：求交集
     *
     * GET /indexName/_search
     * {
     *   "query": {
     *     "match": {
     *       "FIELD": "TEXT"
     *     }
     *   }
     * }
     */
    @Test
    public void testMatchQuery() throws IOException {
        //对华为手机进行分词，然后匹配titile字段，求并集
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "华为手机");
        query.operator(Operator.AND);//默认求并集，可以指定求交集
        //执行查询
        excuteGoodsQuery(query, null,null,null);
    }

    /**
     * 全文检索-多字段查询：mulit_match(任意一个字段符合条件就算符合查询条件)
     * 以下两种查询结果是一样的，为什么？
     * 因为我们将brand、name、business值都利用copy_to复制到了all字段中。因此你根据三个字段搜索，和根据all字段搜索效果当然一样了。
     * 但是，搜索字段越多，对查询性能影响越大，因此建议采用copy_to，然后单字段查询的方式。
     *
     * match和multi_match的区别是什么？
     *  - match：根据一个字段查询
     *  - multi_match：根据多个字段查询，参与查询字段越多，查询性能越差
     *
     * 关于高亮：
     *  - 高亮是对关键字高亮，因此**搜索条件必须带有关键字**，而不能是范围这样的查询。
     *  - 默认情况下，**高亮的字段，必须与搜索指定的字段一致**，否则无法高亮
     *  - 如果要对非搜索字段高亮，则需要添加一个属性：require_field_match=false
     *
     * DSL:
     *      GET /indexName/_search
     *      {
     *        "query": {
     *          "multi_match": {
     *            "query": "文字",
     *            "fields": ["FIELD1", " FIELD12"]
     *          }
     *        }
     *      }
     *
     *      GET /indexName/_search
     *      {
     *        "query": {
     *          "match": {
     *            "all": "如家"                           //高亮一定要使用全文检索查询
     *          }
     *        },
     *        "highlight": {
     *          "fields": {                             // 指定要高亮的字段
     *            "name": {
     *              "pre_tags": "<em>",                 // 用来标记高亮字段的前置标签
     *              "post_tags": "</em>",               // 用来标记高亮字段的后置标签
     *              "require_field_match": "false"     //高亮字段是name，不是搜索字段，默认不会高亮，要想高亮，需要加该配置
     *            }
     *          }
     *        }
     *      }
     */
    @Test
    public void testMutiMatchQuery() throws IOException {
        MultiMatchQueryBuilder multiQueryBuilder = QueryBuilders.multiMatchQuery("外滩如家", "name", "brand", "business");
        //MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("all", "外滩如家");

        HighlightBuilder highlighter = new HighlightBuilder();
        //设置name字段中匹配的词条高亮
        highlighter.field("name");
        highlighter.preTags("<em>");
        highlighter.postTags("</em>");
        //对于高亮字段和搜索字段不一致的情况，需要如下配置才会进行高亮处理
        highlighter.requireFieldMatch(false);
        //执行查询
        excuteHotelQuery(multiQueryBuilder, null,highlighter,null);
    }

    /**
     * 高亮查询：
     * 1. 设置高亮三要素（默认前后缀 ：em）
     *      高亮字段
     *      前缀
     *      后缀
     * 2. 将高亮了的字段数据，替换原有数据
     */
    @Test
    public void testHighLightQuery() throws IOException {
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");

        HighlightBuilder highlighter = new HighlightBuilder();
        //设置title字段中匹配的词条高亮
        highlighter.field("title");
        highlighter.preTags("<font color='red'>");
        highlighter.postTags("</font>");
        //执行查询
        excuteGoodsQuery(query, null,highlighter,null);
    }

    /**
     * 精确查询-词条查询 : termQuery（对传入的查询条件进行词条的等值匹配）
     * 精确查询一般是查找keyword、数值、日期、boolean等类型字段。所以不会对搜索条件分词。常见的有：
     *      - term：根据词条精确值查询
     *      - range：根据值的范围查询
     *
     * 因为精确查询的字段搜是不分词的字段，因此查询的条件也必须是**不分词**的词条。
     * 查询时，用户输入的内容跟自动值完全匹配时才认为符合条件。如果用户输入的内容过多，反而搜索不到数据。
     *
     * DSL：
     *      GET /indexName/_search
     *      {
     *        "query": {
     *          "term": {
     *            "FIELD": {
     *              "value": "文本"
     *            }
     *          }
     *        }
     *      }
     */
    @Test
    public void testTermQuery1() throws IOException {
        QueryBuilder query = QueryBuilders.termQuery("title", "华为");//term词条查询
        excuteGoodsQuery(query, null,null,null);
    }
    @Test
    public void testTermQuery2() throws IOException {
        QueryBuilder query = QueryBuilders.termQuery("city", "上海");//term词条查询
        //执行查询
        excuteHotelQuery(query, null,null,null);
    }

    /**
     * 精确查询-范围查询：rangeQuery 根据值的范围进行查询，可以排序
     *
     * DSL:
     *      GET /indexName/_search
     *      {
     *        "query": {
     *          "range": {
     *            "FIELD": {
     *              "gte": 10, // 这里的gte代表大于等于，gt则代表大于
     *              "lte": 20 // lte代表小于等于，lt则代表小于
     *            }
     *          }
     *        }
     *      }
     */
    @Test
    public void testRangeQuery1() throws IOException {
        //范围查询
        RangeQueryBuilder query = QueryBuilders.rangeQuery("price").gte(2000).lte(3000);
        //指定分页(默认20条)
        Page page = new Page(1,30,new Order("price",Direction.DESC));
        //执行查询
        excuteGoodsQuery(query, page,null,null);
    }

    @Test
    public void testRangeQuery2() throws IOException {
        //范围查询
        RangeQueryBuilder query = QueryBuilders.rangeQuery("price").gte(1000).lte(3000);
        //指定分页(默认20条)
        Page page = new Page(1,30,new Order("price",Direction.DESC));

        //执行查询
        excuteHotelQuery(query, page,null,null);
    }

    /**
     * 模糊查询 - WildcardQuery: *、？通配符匹配查询
     *  ?（任意单个字符） 和  * （0个或多个字符）如：
     *  "*华*"  包含华字的
     *  "华*"   华字后边多个字符
     *  "华?"   华字后边一个字符
     *  "*华"或"?华" 注意：这两个会引发全表（全索引）扫描 注意效率问题
     */
    @Test
    public void testWildcardQuery() throws IOException {
        WildcardQueryBuilder query = QueryBuilders.wildcardQuery("title", "华*");
        //执行查询
        excuteGoodsQuery(query, null,null,null);
    }

    /**
     * 模糊查询 - regexpQuery:正则匹配查询（用得比较少）
     */
    @Test
    public void testRegexpQuery() throws IOException {
        //以单个字符开头的
        RegexpQueryBuilder query = QueryBuilders.regexpQuery("title", "\\w+(.)*");
        //执行查询
        excuteGoodsQuery(query, null,null,null);
    }

    /**
     * 模糊查询 - perfixQuery：前缀匹配查询，对keyword类型字段支持的比较好
     */
    @Test
    public void testPrefixQuery() throws IOException {
        PrefixQueryBuilder query = QueryBuilders.prefixQuery("brandName", "三");
        //执行查询
        excuteGoodsQuery(query, null,null,null);
    }

    /**
     * queryString:多字段同时匹配查询，求并集
     * 1、会对查询条件进行分词。
     * 2、然后将分词后的查询条件和词条进行等值匹配
     * 3、默认取并集（OR）
     * 4、可以指定多个查询字段
     * 5、可识别query中的连接符（or 、and）
     */
    @Test
    public void testQueryStringQuery() throws IOException {
        //对查询条件进行分词，形成"华为","荣耀"。
        //三个字段中任一字段包含华为或荣耀即可，求并集
        //QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("华为荣耀").field("title").field("categoryName").field("brandName").defaultOperator(Operator.OR);
        //三个字段中任一字段包含华为或荣耀即可，求并集
        //QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("华为 OR 荣耀").field("title").field("categoryName").field("brandName");
        //三个字段中有任一一个字段同时包含华为和荣耀
        //QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("华为手机").field("title").field("categoryName").field("brandName").defaultOperator(Operator.AND);
        //三个字段联合起来包含华为或荣耀即可
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("华为 AND 手机").field("title").field("categoryName").field("brandName");

        //simple与以上类似，只是查询条件中不能包含and or这样的连接符了
        //SimpleQueryStringBuilder query = QueryBuilders.simpleQueryStringQuery("华为手机").field("title").field("categoryName").field("brandName").defaultOperator(Operator.AND);
        //SimpleQueryStringBuilder query = QueryBuilders.simpleQueryStringQuery("华为手机").field("title").field("categoryName").field("brandName").defaultOperator(Operator.OR);
        //执行查询
        excuteGoodsQuery(query, new Page(1,800),null,null);
    }


    /**
     * 地理位置查询 - 矩形范围查询：geo_bounding_box，查询坐标落在某个矩形范围的所有文档
     * 查询时，需要指定矩形的**左上**、**右下**两个点的坐标，然后画出一个矩形，落在该矩形内的都是符合条件的点。
     *
     * DSL:
     *     GET /indexName/_search
     *     {
     *       "query": {
     *         "geo_bounding_box": {
     *           "FIELD": {
     *             "top_left": { // 左上点
     *               "lat": 31.1,
     *               "lon": 121.5
     *             },
     *             "bottom_right": { // 右下点
     *               "lat": 30.9,
     *               "lon": 121.7
     *             }
     *           }
     *         }
     *       },
     *       "sort": [
     *        {
     *          "price": "asc"  // 普通字段排序，排序方式ASC、DESC , 排序条件是一个数组，也就是可以写多个排序条件。按照声明的顺序，当第一个条件相等时，再按照第二个条件排序，以此类推
     *        }
     *       ]
     *     }
     */
    @Test
    public void testGeoQuery1() throws IOException {
        //范围查询
        GeoBoundingBoxQueryBuilder query = QueryBuilders.geoBoundingBoxQuery("location")
                .setCorners(new GeoPoint(31.1,121.5),new GeoPoint(30.9,121.7));
        //指定分页(默认20条)
        Page page = new Page(1,30,new Order("price",Direction.ASC));

        //执行查询
        excuteHotelQuery(query, page,null,null);
    }

    /**
     * 地理位置查询 - 圆形范围查询：geo_distance，查询到指定中心点小于某个距离值的所有文档。
     * 在地图上找一个点作为圆心，以指定距离为半径，画一个圆，落在圆内的坐标都算符合条件：
     *
     * DSL:
     *     GET /indexName/_search
     *     {
     *       "query": {
     *         "geo_distance": {
     *           "FIELD": "31.21,121.5", // 圆心
     *           "distance": "15km" // 半径
     *         }
     *       },
     *       "sort": [
     *         {
     *           "_geo_distance" : {        //地理位置坐标排序
     *             "FIELD" : "纬度，经度",   // 文档中geo_point类型的字段名、目标坐标点
     *             "order" : "asc",             // 排序方式
     *             "unit" : "km"            // 排序的距离单位
     *           }
     *         }
     *       ]
     *     }
     *
     * 这个查询的含义是：
     *      - 指定一个坐标为圆心，搜索半径15公里范围内的数据，
     *      - 然后在排序中指定一个坐标点作为目标点，计算每一个文档中，指定字段（必须是geo_point类型）的坐标到目标点的距离是多少
     *      - 根据距离排序
     */
    @Test
    public void testGeoQuery2() throws IOException {
        //范围查询
        GeoDistanceQueryBuilder query = QueryBuilders.geoDistanceQuery("location")
                .point(new GeoPoint("31.21,121.5"))
                .distance(15, DistanceUnit.KILOMETERS);
        //指定分页(默认20条)
        Page page = new Page(1,30);
        GeoDistanceSortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort(query.fieldName(),query.point())
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS);
        //执行查询
        excuteHotelQuery(query, page,null,geoDistanceSortBuilder);
    }

    /**
     * 复合查询 - 相关性算分查询：fuction score，可以控制文档相关性算分，控制文档排名
     * 复合（compound）查询：复合查询可以将其它简单查询组合起来，实现更复杂的搜索逻辑。常见的有两种：
     *      - fuction score：算分函数查询，可以控制文档相关性算分，控制文档排名
     *      - bool query：布尔查询，利用逻辑关系组合多个其它的查询，实现复杂搜索
     *
     * 打分算法：
     *  TF 词频 = 词条出现次数/文档中词条的总数
     *  IDF 逆文档频率 = log（文档总数/包含词条的文档总数）
     *  es 5.1 以前：TF-IDF 算法： score = TF * IDF
     *  es 5.1 以后：BM25算法，在TF-IDF的基础上能降低词频过大对算分的影响，让词频得分曲线更平滑
     *
     * function score的运行流程如下：
     *  - 1）根据**原始条件**查询搜索文档，并且计算相关性算分，称为**原始算分**（query score）
     *  - 2）根据**过滤条件**，过滤文档
     *  - 3）符合**过滤条件**的文档，基于**算分函数**运算，得到**函数算分**（function score）
     *  - 4）将**原始算分**（query score）和**函数算分**（function score）基于**运算模式**做运算，得到最终结果，作为相关性算分。
     *
     * 因此，其中的关键三要素是：
     *  - 过滤条件：决定哪些文档的算分被修改
     *  - 算分函数：决定函数算分的算法：如何计算function score
     *  - 运算模式：决定最终算分结果：function score 与 query score如何运算
     *
     * DSL:
     *     GET /indexName/_search
     *     {
     *      "query":{
     *        "function_score":{
     *          "query": {                        //原始查询条件:搜索文档并基于BM25算法给文档打分，得到query_score原始算分
     *            "match":{"all":"外滩"}
     *          },
     *          "functions":[
     *           {
     *            "filter":{"term":{"id":"60487"}},   //过滤条件:符合条件的文档才会被重新打分
     *            "weight":10                       //算分函数:符合filter条件的文档要根据这个函数做运算,得到的函数算分（function_score），将来会与query score运算，得到新的分数，常见算分函数有：weight(常量值)、field_value_factor(用文档中某个字段的值)、random_score(随机值)、script_score(自定义计算公式)
     *           }
     *          ],
     *          "boost_mode":"multiply"            //运算模式：定义function_score和query_score的运算方式，包括：multiply(默认，两者相乘)、replace(用function_score替换query_score)，其他（sum、avg、max、min）
     *        }
     *       }
     *     }
     */
    @Test
    public void testFunctionScoreQuery() throws IOException {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("all", "外滩");
        //相关性算分查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                matchQueryBuilder, // 原始查询
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("id", "60487"), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10) // 算分函数
                        )
                }
        ).boostMode(CombineFunction.SUM);

        //指定分页(默认20条)
        Page page = new Page(1,30,new Order("price",Direction.DESC));

        //执行查询
        excuteHotelQuery(functionScoreQuery, page,null,null);
    }

    /**
     * 复合查询 - 布尔查询：boolQuery
     * 每一个不同的字段，其查询的条件、方式都不一样，必须是多个不同的查询，而要组合这些查询，就必须用bool查询了
     * 需要注意的是，搜索时，参与**打分的字段越多，查询的性能也越差**。因此这种多条件查询时，建议这样做：
     *      - 搜索框的关键字搜索，是全文检索查询，使用must查询，参与算分
     *      - 其它过滤条件，采用filter查询。不参与算分
     *
     * •must（and）：条件必须成立，类似“与”
     * •must_not（not）：条件必须不成立，不参与算分
     * •should（or）：条件可以成立，选择性匹配，类似“或”
     * •filter：条件必须成立，性能比must高。不参与算分
     *
     * 得分:即条件匹配度,匹配度越高，得分越高，must和filter配合使用时，max_score（得分）是显示的
     *
     * 需求：查询商品
     * 1. 查询品牌名称为:华为
     * 2. 查询标题包含：手机
     * 3. 查询价格在：2000-3000
     *
     * DSL:
     *      GET /goods/_search
     *       {
     *         "query": {
     *           "bool": {
     *             "must": [
     *               {"term": {"brandName": "华为" }}
     *             ],
     *             "filter": [
     *                { "match": {"title": "手机" }},
     *                { "range": {"price": { "gte": 2000,"lte": 3000} }}
     *             ]
     *           }
     *         }
     *       }
     */
    @Test
    public void testBoolQuery1() throws IOException {
        //1.构建boolQuery
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //2.构建各个查询条件
        //2.1 查询品牌名称为:华为
        QueryBuilder termQuery = QueryBuilders.termQuery("brandName", "华为");
        query.must(termQuery);

        //2.2. 查询标题包含：手机
        QueryBuilder matchQuery = QueryBuilders.matchQuery("title", "手机");
        query.filter(matchQuery);

        //2.3 查询价格在：2000-3000
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price").gte(2000).lte(3000);
        query.filter(rangeQuery);

        //3.执行查询
        excuteGoodsQuery(query, null,null,null);
    }

    /**
     * 需求：查询酒店
     * 1. 查询城市在:上海
     * 2. 查询酒店品牌是：皇冠假日或华美达
     * 3. 查询价格：大于500
     * 4、查询评分，大于45
     *
     * DSL：
     *      GET /hotel/_search
     *      {
     *        "query": {
     *          "bool": {
     *            "must": [
     *              {"term": {"city": "上海" }}
     *            ],
     *            "should": [
     *              {"term": {"brand": "皇冠假日" }},
     *              {"term": {"brand": "华美达" }}
     *            ],
     *            "must_not": [
     *              { "range": { "price": { "lte": 500 } }}
     *            ],
     *            "filter": [
     *              { "range": {"score": { "gte": 45 } }}
     *            ]
     *          }
     *        }
     *      }
     */
    @Test
    public void testBoolQuery2() throws IOException {
        //1.构建boolQuery
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //2.构建各个查询条件
        //2.1 查询城市名称为:上海
        QueryBuilder termQuery = QueryBuilders.termQuery("city", "上海");
        query.must(termQuery);

        //2、2 查询酒店品牌是皇冠假日或华美达
        QueryBuilder termQueryBrand1 = QueryBuilders.termQuery("brand", "皇冠假日");
        QueryBuilder termQueryBrand2 = QueryBuilders.termQuery("brand", "华美达");
        query.should(termQueryBrand1).should(termQueryBrand2);

        //2.3 查询价格不小于500
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price").lte(500);
        query.mustNot(rangeQuery);

        //2.4. 查询评分大于等于45
        QueryBuilder matchQuery = QueryBuilders.rangeQuery("score").gte(45);
        query.filter(matchQuery);

        //3.执行查询
        excuteHotelQuery(query, null,null,null);
    }


    /**
     * DSL:
     *    GET /hotel/_search
     *    {
     *     "query":{
     *       "function_score":{
     *         "query": {
     *            "bool": {
     *              "must": [
     *                {"term": {"city": "上海" }}
     *              ],
     *              "should": [
     *                {"term": {"brand": "喜来登" }},
     *                {"term": {"brand": "君悦" }},
     *                {"term": {"brand": "万怡" }}
     *              ],
     *              "must_not": [
     *                { "range": { "price": { "lte": 200 } }}
     *              ],
     *              "filter": [
     *                { "range": {"score": { "gte": 45 } }},
     *                {"geo_distance": {
     *                    "location": "31.21,121.5",
     *                    "distance": "15km"
     *                  }
     *                }
     *              ]
     *            }
     *         },
     *         "functions":[
     *          {
     *           "filter":{"term":{"brand":"君悦"}},
     *           "weight":10
     *          }
     *         ],
     *         "boost_mode":"sum"
     *       }
     *      },
     *      "from": 0,
     *      "size": 30,
     *      "highlight": {
     *        "fields": {
     *          "name": {
     *            "pre_tags": "<font 'color = red'>",
     *            "post_tags": "</font>",
     *            "require_field_match": "false"
     *          }
     *        }
     *      },
     *      "sort": [
     *        {
     *          "_geo_distance" : {
     *            "location" : "31.21,121.5",
     *            "order" : "asc",
     *            "unit" : "km"
     *          }
     *        },
     *        {"price": "asc"}
     *      ]
     *    }
     */
    @Test
    public void testAllQuery() throws IOException {
        //1.构建boolQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //2.构建各个查询条件
        //2.1 查询城市名称为:上海
        QueryBuilder termQuery = QueryBuilders.termQuery("city", "上海");
        boolQuery.must(termQuery);

        //2、2 查询酒店品牌是皇冠假日或华美达
        QueryBuilder termQueryBrand1 = QueryBuilders.termQuery("brand", "喜来登");
        QueryBuilder termQueryBrand2 = QueryBuilders.termQuery("brand", "君悦");
        QueryBuilder termQueryBrand3 = QueryBuilders.termQuery("brand", "万怡");
        boolQuery.should(termQueryBrand1).should(termQueryBrand2).should(termQueryBrand3);

        //2.3 查询价格不小于200
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price").lte(200);
        boolQuery.mustNot(rangeQuery);

        //2.4. 查询评分大于等于45
        QueryBuilder matchQuery = QueryBuilders.rangeQuery("score").gte(45);
        boolQuery.filter(matchQuery);

        //范围查询
        GeoDistanceQueryBuilder geoQuery = QueryBuilders.geoDistanceQuery("location")
                .point(new GeoPoint("31.21,121.5"))
                .distance(15, DistanceUnit.KILOMETERS);
        boolQuery.filter(geoQuery);


        //3、相关性算分查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery, // 原始查询
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("brand", "君悦"), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10) // 算分函数
                        )
                }
        ).boostMode(CombineFunction.SUM);

        //指定分页
        Page page = new Page(1,30,new Order("price",Direction.DESC));

        HighlightBuilder highlighter = new HighlightBuilder();
        //设置title字段中匹配的词条高亮
        highlighter.field("name");
        highlighter.preTags("<font color='red'>");
        highlighter.postTags("</font>");
        highlighter.requireFieldMatch(false);

        GeoDistanceSortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort("location", new GeoPoint("31.21,121.5"))
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS);

        //3.执行查询
        excuteHotelQuery(functionScoreQuery, page,highlighter,geoDistanceSortBuilder);
    }

    /**
     * 指标聚合查询 :相当于MySQL的聚合函数。max、min、avg、sum等
     *
     * 1. 查询title包含手机的数据
     * 2. 查询价格最高的
     */
    @Test
    public void testAggQuery1() throws IOException {
        //查询条件：根据手机匹配titile字段
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");
        //创建统计Builder对象，查询price最大值，起个别名叫：max_price
        AggregationBuilder agg = AggregationBuilders.max("max_price").field("price");
        //执行聚合查询，返回聚合结果Map
        Map<String, Aggregation> aggregationMap = this.getAggregationMap("goods",query,agg);

        ParsedMax max_price = (ParsedMax) aggregationMap.get("max_price");
        double maxPrice = max_price.getValue();
        System.out.println( maxPrice);
    }

    /**
     * 查询聚合结果
     * @param indexName 索引名称
     * @param query 查询条件对象
     * @param aggs 聚合参数列表
     * @return
     * @throws IOException
     */
    private Map<String, Aggregation> getAggregationMap(String indexName,QueryBuilder query,AggregationBuilder ... aggs) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1、指定查询条件和分页
        sourceBulider.query(query);
        sourceBulider.from(0);
        sourceBulider.size(100);

        // 2. 指定聚合参数
        Arrays.stream(aggs).forEach(agg -> sourceBulider.aggregation(agg));

        searchRequest.source(sourceBulider);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();

        //3、获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<ESGoods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            //转为java
            ESGoods goods = JSON.parseObject(sourceAsString, ESGoods.class);
            goodsList.add(goods);
        }
        goodsList.forEach(System.out::println);

        //4、获取聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> aggregationMap = aggregations.asMap();
        return aggregationMap;
    }

    /**
     * 桶聚合查询 :相当于MySQL的 group by 操作。不要对text类型的数据进行分组，会失败
     *
     * 1. 查询title包含手机的数据
     * 2. 查询品牌列表
     */
    @Test
    public void testAggQuery2() throws IOException {
        //查询条件：根据手机匹配titile字段
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");
        //创建统计Builder对象，根据brandName分组，查询brandName列表，起个别名叫：goods_brands
        AggregationBuilder agg1 = AggregationBuilders.terms("goods_brands").field("brandName");
        //创建统计Builder对象，查询price最大值，起个别名叫：max_price
        AggregationBuilder agg2 = AggregationBuilders.max("max_price").field("price");
        //执行聚合查询，返回聚合结果Map
        Map<String, Aggregation> aggregationMap = this.getAggregationMap("goods",query,agg1,agg2);
        //获取桶聚合结果
        Terms goods_brands = (Terms) aggregationMap.get("goods_brands");
        List<? extends Terms.Bucket> buckets = goods_brands.getBuckets();

        List brands = new ArrayList();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            brands.add(key);
        }
        brands.stream().forEach(System.err::println);

        //获取指标聚合结果
        ParsedMax max_price = (ParsedMax) aggregationMap.get("max_price");
        double maxPrice = max_price.getValue();
        System.err.println( maxPrice);
    }

    private void excuteGoodsQuery(QueryBuilder query, Page page, HighlightBuilder hlBuilder,GeoDistanceSortBuilder distanceSortBuilder) throws IOException {
        List<ESGoods> goods = (List<ESGoods>)esSearchCommonService.excuteQuery("goods", ESGoods.class,
                query, page, hlBuilder,distanceSortBuilder).get("result");
        goods.forEach(System.err::println);
    }

    private void excuteHotelQuery(QueryBuilder query, Page page, HighlightBuilder hlBuilder,GeoDistanceSortBuilder distanceSortBuilder) throws IOException {
        List<HotelDoc> hotels = (List<HotelDoc>)esSearchCommonService.excuteQuery("hotel", HotelDoc.class,
                query, page, hlBuilder,distanceSortBuilder).get("result");
        hotels.forEach(System.err::println);
    }
}
