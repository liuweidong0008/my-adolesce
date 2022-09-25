package com.adolesce.cloud.es;

import cn.hutool.core.bean.BeanUtil;
import com.adolesce.cloud.dubbo.api.db.ESGoodsApi;
import com.adolesce.cloud.dubbo.api.db.EsHotelApi;
import com.adolesce.cloud.dubbo.domain.db.ESGoods;
import com.adolesce.cloud.dubbo.domain.db.EsHotel;
import com.adolesce.cloud.dubbo.domain.es.HotelDoc;
import com.adolesce.cloud.dubbo.domain.es.Person;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adolesce.cloud.es.constants.IndexMappingConstants.*;

/**
 * ES Java基础操作API
 * 1、索引的增、删、查
 * 2、文档的增、删、改、查
 *
 * 索引库操作有哪些？
 *     -1、创建索引库：PUT /索引库名
 *     -2、查询索引库：GET /索引库名
 *     -3、删除索引库：DELETE /索引库名
 *     -4、查询映射：GET/索引库名/_mapping
 *     -5、添加字段：PUT /索引库名/_mapping
 *
 * 索引库操作的基本步骤：
 *     -1、初始化RestHighLevelClient
 *     -2、创建XxxIndexRequest。XXX是Create、Get、Delete
 *     -3、准备DSL（ Create时需要，其它是无参）
 *     -4、发送请求。调用RestHighLevelClient#indices().xxx()方法，xxx是create、exists、delete
 *
 * 文档操作有哪些？
 *     -1、创建文档：POST /{索引库名}/_doc/文档id   { json文档 }
 *     -2、查询文档：GET /{索引库名}/_doc/文档id
 *     -3、删除文档：DELETE /{索引库名}/_doc/文档id
 *     -4、修改文档：
 *         - 全量修改：PUT /{索引库名}/_doc/文档id { json文档 }
 *         - 增量修改：POST /{索引库名}/_update/文档id { "doc": {字段}}
 *
 * 文档操作的基本步骤：
 *     -1、初始化RestHighLevelClient
 *     -2、创建XxxRequest。XXX是Index、Get、Update、Delete、Bulk
 *     -3、准备参数（Index、Update、Bulk时需要）
 *     -4、发送请求。调用RestHighLevelClient#.xxx()方法，xxx是index、get、update、delete、bulk
 *     -5、解析结果（Get时需要）
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsIndexAndDocTest {
    @Autowired
    private RestHighLevelClient client;
    @DubboReference
    private ESGoodsApi esGoodsApi;
    @DubboReference
    private EsHotelApi hotelApi;

    /**
     * 1、关于ES索引：是同一类型文档的集合，可看做是mysql的一张表
     * 2、关于ES映射：是索引中字段类型和名称等属性的约束，可看做是mysql表结构
     * 3、ES索引只能增、删、查，不支持修改，比如索引名称之类的
     * 4、ES映射和其中的字段只能增、查，不能修改和删除
     */

    /**
     * 单纯创建索引-不同步指定mapping映射
     * DSL：
     * PUT 索引名
     */
    @Test
    public void testCreateIndex() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();
        //2.构建 创建索引请求对象
        CreateIndexRequest createRequest = new CreateIndexRequest("itheima");
        //3、执行创建，获取响应对象
        CreateIndexResponse response = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.err.println("索引创建结果：" + (response.isAcknowledged() ? "成功" : "失败"));
    }


    /**
     * 删除索引
     * DSL：
     *  DELETE 索引名
     */
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("itheima");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.err.println("索引删除结果：" + response.isAcknowledged());
    }


    /**
     * 创建索引同时指定mapping映射，常见的mapping属性包括：
     * - type：字段数据类型，常见的简单类型有：
     * - 字符串：text（可分词的文本）、keyword（精确值，例如：品牌、国家、ip地址）
     * - 数值：long、integer、short、byte、double、float、
     * - 布尔：boolean
     * - 日期：date
     * - 对象：object
     * - index：是否创建索引，默认为true
     * - analyzer：使用哪种分词器
     * - properties：该字段的子字段
     * <p>
     * DSL：
     *     PUT /索引库名称
     *     {
     *       "mappings": {
     *         "properties": {
     *           "字段名": {
     *             "type": "text",
     *             "analyzer": "ik_smart"
     *           },
     *           "字段名2": {
     *             "type": "keyword",
     *             "index": "false",
     *             "copy_to": [
     *               "all"
     *             ]
     *           },
     *           "字段名3": {
     *             "properties": {
     *               "子字段": {
     *                 "type": "keyword"
     *               }
     *             }
     *           }
     *         }
     *       }
     *     }
     *
     */
    @Test
    public void testCreateIndexAndMapping1() throws IOException {
        CreateIndexRequest createRequest = new CreateIndexRequest("itcast");
        //设置映射
        createRequest.mapping(ITCAST_MAPPING, XContentType.JSON);
        CreateIndexResponse response = client.indices().create(createRequest, RequestOptions.DEFAULT);
        //根据返回值判断结果
        System.err.println("索引创建结果：" + response.isAcknowledged());
    }

    @Test
    public void testCreateIndexAndMapping2() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        request.source(HOTEL_MAPPING, XContentType.JSON);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.err.println("索引创建结果：" + response.isAcknowledged());
    }

    /**
     * 查询索引是否存在
     */
    @Test
    public void testExistIndex1() throws IOException {
        GetIndexRequest getRequest = new GetIndexRequest("itcast");
        boolean isExists = client.indices().exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(isExists ? "存在" : "不存在");
    }

    public boolean testExistIndex2() throws IOException {
        // 1.准备Request
        GetIndexRequest request = new GetIndexRequest("hotel");
        // 3.发送请求
        boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(isExists ? "存在" : "不存在");
        return isExists;
    }


    /**
     * 查询索引
     * DSL：
     *      GET 索引名
     */
    @Test
    public void testQueryIndex() throws IOException {
        IndicesClient indices = client.indices();
        GetIndexRequest getReqeust = new GetIndexRequest("itcast");
        GetIndexResponse response = indices.get(getReqeust, RequestOptions.DEFAULT);
        //获取结果
        Map<String, MappingMetadata> mappings = response.getMappings();
        for (String key : mappings.keySet()) {
            System.err.println(key + ":" + mappings.get(key).getSourceAsMap());
        }
    }


    /*2、关于文档：可以增、删、改、查*/

    /**
     * 添加文档时，如果添加的字段映射不保存在，会自动新增映射，但是不太友好，所以映射最好还是自定义创建
     * 添加文档：使用map作为数据
     * DSL:
     *     POST /索引库名/_doc/文档id
     *     {
     *      "字段1": "值1",
     *       "字段2": "值2",
     *      "字段3": {
     *       "子属性1": "值3",
     *       "子属性2": "值4"
     *      },
     *     // ...
     *     }
     */
    @Test
    public void testAddDocument1() throws IOException {
        //数据对象，map
       /* Map data = new HashMap();
        data.put("address", "北京昌平");
        data.put("name", "小胖");
        data.put("age", 20);
        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast").id("1").source(data);*/

        //对象转成Map后进行保存
        Person p = new Person();
        p.setId("2");
        p.setName("中胖");
        p.setAge(25);
        p.setAddress("湖北武汉");
        Map data = BeanUtil.beanToMap(p);

        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast").id(p.getId()).source(data);

        //添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        //打印响应结果
        System.err.println(response.getId());
    }

    /**
     * 添加文档,使用对象作为数据
     */
    @Test
    public void testAddDocument2() throws IOException {
        //数据对象，javaObject
        Person p = new Person();
        p.setId("3");
        p.setName("大胖");
        p.setAge(30);
        p.setAddress("陕西西安");

        //将对象转为json
        String data = JSON.toJSONString(p);

        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast").id(p.getId()).source(data, XContentType.JSON);
        //2、添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        //3、打印响应结果
        System.err.println(response.getId());
    }

    @Test
    public void testAddDocument3() throws IOException {
        // 1.查询数据库hotel数据
        EsHotel hotel = hotelApi.getById(61083L);
        // 2.转换为HotelDoc
        HotelDoc hotelDoc = new HotelDoc(hotel);
        // 3.转JSON
        String json = JSON.toJSONString(hotelDoc);

        // 1.准备Request
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        // 2.准备请求参数DSL，其实就是文档的JSON字符串
        request.source(json, XContentType.JSON);
        // 3.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据id删除文档
     * DSL:
     *      DELETE /{索引库名}/_doc/id值
     */
    @Test
    public void testDelDocumentById1() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("itcast", "1");
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.err.println(response.getId());
    }

    @Test
    public void testDelDocumentById2() throws IOException {
        // 1.准备Request      // DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest("hotel", "61083");
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 全量修改文档：跟新增一样，略。在添加文档时，如果id存在则修改，id不存在则添加
     * DSL：
     *    PUT /{索引库名}/_doc/文档id
     *    {
     *      "字段1": "值1",
     *      "字段2": "值2",
     *      // ... 略
     *    }
     */
    @Test
    public void testUpdateDocument1() {
    }

    /**
     * 局部更新文档
     * DSL:
     *      POST /{索引库名}/_update/文档id
     *      {
     *          "doc": {
     *          "字段名": "新的值"
     *          }
     *      }
     */
    @Test
    public void testUpdateDocument2() throws IOException {
        // 1.准备Request
        UpdateRequest request = new UpdateRequest("hotel", "61083");
        // 2.准备参数
        request.doc(
                "price", "870"
        );
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据id查询文档
     * DSL：
     *      GET /{索引库名称}/_doc/{id}
     */
    @Test
    public void testFindDocumentById1() throws IOException {
        GetRequest getReqeust = new GetRequest("itcast", "1");
        //getReqeust.id("1"); //这儿指定id也可以
        GetResponse response = client.get(getReqeust, RequestOptions.DEFAULT);
        //获取数据对应的json
        System.err.println(response.getSourceAsString());
    }

    @Test
    public void testFindDocumentById2() throws IOException {
        // 1.准备Request      // GET /hotel/_doc/{id}
        GetRequest request = new GetRequest("hotel", "61083");
        // 2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();

        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println("hotelDoc = " + hotelDoc);
    }


    /**
     * 根据id查询文档是否存在
     */
    @Test
    public void testExistsDoc() throws IOException {
        GetRequest getRequest = new GetRequest("itcast", "2");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.err.println(exists);
    }

    /**
     * 1. 批量操作 bulk(大量)
     * bulk批量操作是将文档的增删改查一系列操作，通过一次请求全部做完，以减少网络传输次数
     */
    @Test
    public void testBulk() throws IOException {
        //创建bulkrequest对象，整合所有操作
        BulkRequest bulkRequest = new BulkRequest();

        /*
        # 1. 删除8号记录
        # 2. 添加5号记录
        # 3. 修改2号记录 名称为 “李四”
         */
        //添加对应操作
        //1. 删除8号记录
        DeleteRequest deleteRequest = new DeleteRequest("person", "8");
        bulkRequest.add(deleteRequest);

        //2. 添加5号记录
        Map map = new HashMap();
        map.put("name", "五号");
        map.put("age", "18");
        map.put("address", "北京");
        IndexRequest indexRequest = new IndexRequest("person").id("5").source(map);
        bulkRequest.add(indexRequest);

        Map map2 = new HashMap();
        map2.put("name", "王五");
        //3. 修改2号记录 名称为 “王五”
        UpdateRequest updateReqeust = new UpdateRequest("person", "2").doc(map2);
        bulkRequest.add(updateReqeust);

        //执行批量操作
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        RestStatus status = response.status();
        System.out.println(status);
    }


    /**
     * 利用bulk进行商品数据库数据的批量导入
     */
    @Test
    public void testImportGoodsDataByBulk() throws IOException {
        //1.查询所有数据，mysql
        List<ESGoods> goodsList = esGoodsApi.selectList();

        //2、新建goos索引
        IndicesClient indicesClient = client.indices();
        CreateIndexRequest createRequest = new CreateIndexRequest("goods");
        createRequest.mapping(GOODS_MAPPING, XContentType.JSON);
        CreateIndexResponse indexResponse = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.err.println("索引创建结果：" + indexResponse.isAcknowledged());

        //4.bulk导入
        BulkRequest bulkRequest = new BulkRequest();

        //4.1 循环goodsList，创建IndexRequest添加数据
        for (ESGoods goods : goodsList) {
            //2.2 设置spec规格信息 Map的数据   specStr:{}
            //goods.setSpec(JSON.parseObject(goods.getSpecStr(),Map.class));
            String specStr = goods.getSpecStr();
            //将json格式字符串转为Map集合
            Map map = JSON.parseObject(specStr, Map.class);
            //设置spec map
            goods.setSpec(map);

            //将goods对象转换为json字符串
            String data = JSON.toJSONString(goods);//map --> {}
            IndexRequest indexRequest = new IndexRequest("goods");
            indexRequest.id(goods.getId() + "").source(data, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * 利用bulk进行酒店数据库数据的批量导入
     */
    @Test
    public void testImportHotelDataByBulk() throws IOException {
        if(!testExistIndex2()){
            testCreateIndexAndMapping2();
        }

        // 查询所有的酒店数据
        List<EsHotel> list = hotelApi.list();

        // 1.准备Request
        BulkRequest request = new BulkRequest();
        // 2.准备参数
        for (EsHotel hotel : list) {
            // 2.1.转为HotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            // 2.2.转json
            String json = JSON.toJSONString(hotelDoc);
            // 2.3.添加请求
            request.add(new IndexRequest("hotel").id(hotel.getId().toString()).source(json, XContentType.JSON));
        }
        // 3.发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }

    /**
     * map转json方式得到mapping结构
     *
     * @return
     */
    private String getMapping() {
        Map<String, Object> propertiesMap = new HashMap<>();
        Map<String, Object> attributeMap = new HashMap<>();

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("type", "text");
        keyMap.put("analyzer", "ik_smart");
        attributeMap.put("title", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "double");
        attributeMap.put("price", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "date");
        attributeMap.put("createTime", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "keyword");
        attributeMap.put("categoryName", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "keyword");
        attributeMap.put("brandName", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "object");
        attributeMap.put("spec", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "integer");
        attributeMap.put("saleNum", keyMap);

        keyMap = new HashMap<>();
        keyMap.put("type", "integer");
        attributeMap.put("stock", keyMap);

        propertiesMap.put("properties", attributeMap);

        String mapping = JSON.toJSONString(propertiesMap);
        return mapping;
    }
}
