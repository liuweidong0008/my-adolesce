package com.adolesce.cloud.mongo;

import com.adolesce.cloud.dubbo.domain.mongo.Address;
import com.adolesce.cloud.dubbo.domain.mongo.Location;
import com.adolesce.cloud.dubbo.domain.mongo.MyUser;
import com.adolesce.cloud.mongo.utils.IdWorker;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 注意：
 * 1. MongoDB是没有默认管理员账号，所以需要在开启权限认证后添加管理员账号
 * 2. 切换到admin数据库，添加/新建的账号才是管理员账号
 * 3. 用户只能在用户所属数据库登录，包括管理员账号
 * 4. 管理员可以管理所有数据库，但不能直接管理其他数据库，需在admin数据库认证
 * <p>
 * windows环境安装mogodb，默认是不开启权限模式的，需要进行开启
 * 1、regedit命令进入注册表
 * 2、进入路径：HEEY_LOCAL_MACHINE/SYSTEM/CurrentControlSet/Services/MongoDB
 * 3、右侧，更改ImagePath路径：追加：--auth
 * 4、重启mongodb服务，cmd中，执行net stop mongodb / net start mongodb
 * <p>
 * 用户/授权
 * mongo
 * use admin
 * 创建超级管理员
 * db.createUser({ user: 'admin', pwd: 'admin', roles: [ { role: "root", db: "admin" } ] })
 * 授权进入
 * mongo -u "admin" -p "admin" --authenticationDatabase "admin"
 * 创建普通用户
 * db.createUser({ user: 'lwd', pwd: 'lwd', roles: [ { role: "readWrite", db: "adolesce" } ] });
 * 授权进入
 * mongo -u "lwd" -p "lwd" --authenticationDatabase "admin"
 * <p>
 * <p>
 * mongodb整合步骤
 * 1、引入依赖：spring-boot-starter-data-mongodb
 * 2、application.yaml 配置文件添加mongodb配置
 * 3、编写mongodb实体类
 * 4、注入mongoTemplate进行使用
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoDBTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 新增/批量新增
     */
    @Test
    public void testSaveMyUser() {
       /* MyUser myUser = new MyUser();
        myUser.setId(ObjectId.get());
        myUser.setUserId(ObjectId.get());
        myUser.setUserName("关羽");
        myUser.setSex(1);
        myUser.setAge(20);
        myUser.setAddress(new Address("长沙市", "天心区"));
        this.mongoTemplate.save(myUser);*/
        //动态指定集合名称（MyUser类上注解中注释的代码@Document要放开）
        //this.mongoTemplate.save(myUser,"my_user_" + 1);

        MyUser myUser;
        List<MyUser> userList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            myUser = new MyUser();
            myUser.setId(ObjectId.get());
            myUser.setUserId(ObjectId.get());
            myUser.setUserName("关羽" + i);
            myUser.setSex(1);
            myUser.setAge(20);
            myUser.setAddress(new Address("长沙市", "天心区"));
            userList.add(myUser);
        }
        this.mongoTemplate.insert(userList, MyUser.class);
        //this.mongoTemplate.insertAll(userList);
        //this.mongoTemplate.insertAll();
    }

    /**
     * 删除
     */
    @Test
    public void testRemoveMyUser() {
        //根据条件删除
        /*Query query = Query.query(Criteria.where("age").is(18)
                .and("sex").is(1)
        );
        DeleteResult deleteResult = this.mongoTemplate.remove(query, MyUser.class);*/


        //根据条件删除并返回被删除结果集
        Query query = Query.query(Criteria.where("address.street").is("三叉路"));
        List<MyUser> users = this.mongoTemplate.findAllAndRemove(query, MyUser.class, "my_user");
        //MyUser user = this.mongoTemplate.findAndRemove(query, MyUser.class, "my_user");
        System.err.println(users);
    }

    /**
     * 更新
     * 1、更新已存在的字段
     * 2、不存在的字段自动添加
     * 3、unset删除字段
     * 4、inc：数字字段自增
     */
    @Test
    public void testUpdateMyUser() {

        ////update table set age = 41，sex = 2, isOld = true where user_name = '刘威东'
        Query query = Query.query(Criteria.where("address.city").is("武汉市"));
        Update update = Update.update("age", 12).set("sex", 1)
                .inc("funCount", 1)
                .set("createDate", new Date());
        //.currentDate("createDate");  //为字段设置为当前日期(年月日 时分秒)
        //.rename("create_date","createDate"); //将create_date字段改名为createDate
        //.set("phone","18201093");  //可自动添加不存在的字段
        //.addToSet("phone","18301327332").addToSet("hobby").value("篮球"); //添加集合类型字段
        //.unset("phone");//删除phone字段

        //修改符合条件的所有记录
        //UpdateResult updateResult = this.mongoTemplate.updateMulti(query, update, MyUser.class);

        //修改符合条件的第一条记录
        //UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, "my_user");

        //如果修改的这条记录不存在，新增
        UpdateResult upsertResult = mongoTemplate.upsert(query, update, MyUser.class);
    }

    /**
     * 更新并返回最新记录
     */
    @Test
    public void testFindAndModifyMyUser() {
        Query query = Query.query(Criteria.where("userName").is("刘威东"));
        Update update = Update.update("age", 88).set("sex", 3).set("isOld", true)
                .inc("fenCount", 1);

        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据

        MyUser myUser = this.mongoTemplate.findAndModify(query, update, options, MyUser.class);
        System.err.println(myUser);
    }

    /**
     * 查询数量
     */
    @Test
    public void testQueryMyUserCount() {
        Query query = Query.query(Criteria.where("age").is(12));
        long count = this.mongoTemplate.count(query, MyUser.class);
        System.err.println(count);
    }

    /**
     * 查询指定数据是否存在
     */
    @Test
    public void testExistisByName() {
        Query query = Query.query(Criteria.where("userName").is("刘威东")
                .and("age").is(88)
                .and("userId").is(new ObjectId("6117b8dee1243116385b54a6")));
        boolean exists = this.mongoTemplate.exists(query, MyUser.class);
        System.err.println(exists);
    }


    /**
     * 根据id查询MyUser对象
     * F8 下一行
     * F9 下一个断点处
     * alt+F8 表达式
     * alt+F9 跳到光标处
     * ctrl+F5 重新运行
     */
    @Test
    public void testQueryMyUsersById() {
        MyUser myUser = this.mongoTemplate.findById(new ObjectId("6117b8dee1243116385b54a5"), MyUser.class);
        System.err.println(myUser);

        Query query = Query.query(Criteria.where("_id").is("6117b8dee1243116385b54a5"));
        myUser = this.mongoTemplate.findOne(query, MyUser.class);
        System.err.println(myUser);

        query = Query.query(Criteria.where("id").is("6117b8dee1243116385b54a5"));
        myUser = this.mongoTemplate.findOne(query, MyUser.class);
        System.err.println(myUser);
    }

    /**
     * 条件查询
     */
    @Test
    public void testQueryMyUsersByName() throws ParseException {
        Query query = Query.query(Criteria.where("userName").is("刘威东")
                .and("age").is(88)
                .and("userId").is(new ObjectId("6117b8dee1243116385b54a6")))
                .with(Sort.by(Sort.Order.desc("age"))).limit(1);

        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
    }

    /**
     * 逻辑查询
     */
    @Test
    public void testQueryOrMyUsers() {
        //先构建Criteria对象，再构建Query对象
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("age").is(12), Criteria.where("userName").is("ly"));
        Query query = Query.query(criteria);
        //相当于：where age = 35 or userName = '张三'

        //构建Query对象同时构建Criteria对象
        query = Query.query(Criteria.where("userName").is("lwd")
                .andOperator(Criteria.where("age").is(12), Criteria.where("sex").is(1)));
        // 相当于：where userName = 'lwd' and (age = 12 and sex = 1)
        //.orOperator(Criteria.where("age").is(12), Criteria.where("age").is(88)));
        //相当于：where userName = 'lwd' and (age = 12 or age = 88)

        //追加查询条件
        query.addCriteria(Criteria.where("age").is(12)).addCriteria(Criteria.where("sex").is(1));
        //相当于: and age = 12 and sex = 1

        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);

        //Criteria 对象就是用于条件的封装，可以通过new的形式，进行更专注精准的构造，也可以通过链式编程的形式进行构造
        //criteria.orOperator用于构造逻辑OR条件，里面的条件全部以AND进行连接（A = '' OR B = '' OR C = ''）
        //criteria.andOperator用于构造AND条件，里面的条件全部以AND进行连接（A = '' AND B = '' AND C = ''）
        //query.addCriteria 用于给查询对象动态添加一个criteria条件对象
    }

    /**
     * 分页查询
     */
    @Test
    public void testQueryMyUserPageList() {
        Integer pageNum = 1;//查询第几页
        Integer pageSize = 2;//一页多少条数据
        Query query = new Query().skip((pageNum - 1) * pageSize).limit(pageSize).with(Sort.by(Sort.Order.desc("age")));
        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
    }

    /**
     * 分页查询
     */
    @Test
    public void testQueryMyUserPageLis2() {
        Integer pageNum = 1;//查询第几页
        Integer pageSize = 2;//一页多少条数据
        //分页并且排序参数
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("age")));

        //查询参数
        Query query = Query.query(Criteria.where("userName").is("lwd")).with(pageRequest);

        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
    }

    /**
     * 模糊查询
     */
    @Test
    public void testLikeQueryMyUser() {
        //完全匹配
        Pattern pattern1 = Pattern.compile("^刘威东$", Pattern.CASE_INSENSITIVE);
        Query query = Query.query(Criteria.where("userName").regex(pattern1));
        List<MyUser> myUsers = mongoTemplate.find(query, MyUser.class);
        System.err.println(myUsers);
        //左模糊
        Pattern pattern2 = Pattern.compile("^.*威东", Pattern.CASE_INSENSITIVE);
        query = Query.query(Criteria.where("userName").regex(pattern2));
        myUsers = mongoTemplate.find(query, MyUser.class);
        System.err.println(myUsers);
        //右模糊
        Pattern pattern3 = Pattern.compile("^刘威.*$", Pattern.CASE_INSENSITIVE);
        query = Query.query(Criteria.where("userName").regex(pattern3));
        myUsers = mongoTemplate.find(query, MyUser.class);
        System.err.println(myUsers);
        //左右模糊
        Pattern pattern4 = Pattern.compile("^.*威.*$", Pattern.CASE_INSENSITIVE);
        query = Query.query(Criteria.where("userName").regex(pattern4));
        myUsers = mongoTemplate.find(query, MyUser.class);
        System.err.println(myUsers);
    }

    /**
     * 查询所有
     */
    @Test
    public void testFindMyUserAll() {
        List<MyUser> users = this.mongoTemplate.findAll(MyUser.class);
        System.err.println(users);
    }

    /**
     * MongoDB 生成分布式主键
     */
    @Test
    public void testIdWorker() {
        Long autoId = this.idWorker.getNextId("order");
        System.err.println(autoId);
    }

    /**
     * 指定结果条数随机查询
     */
    @Test
    public void testRandomQuery1() {
        int count = 2;
        //1、创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(MyUser.class, Aggregation.sample(count));
        //2、调用mongoTemplate方法统计
        AggregationResults<MyUser> results = mongoTemplate.aggregate(aggregation, MyUser.class);
        List<MyUser> mappedResults = results.getMappedResults();
        System.out.println(mappedResults);
    }

    /**
     * 指定结果条数和查询条件随机查询
     */
    @Test
    public void testRandomQuery2() {
        int count = 4;
        Criteria criteria = Criteria.where("address.city").is("长沙市");

        //1、创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(
                MyUser.class,
                //先进行条件筛选,一定要注意顺序（筛选与_class无关）
                Aggregation.match(criteria),
                //再进行随机取值
                Aggregation.sample(count)
        );
        //2、调用mongoTemplate方法统计
        AggregationResults<MyUser> results = mongoTemplate.aggregate(aggregation, MyUser.class);
        List<MyUser> mappedResults = results.getMappedResults();
        System.out.println(mappedResults);
    }

    /**
     * 保存地理位置
     */
    @Test
    public void testSaveLocation() {
        double longitude = 116.403972;
        double latitude = 39.915111;
        Location location = new Location();
        location.setUserId(1L);
        location.setAddress("北京天安门");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.404507;
        latitude = 39.915558;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京天安门服务部");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.418071;
        latitude = 39.91443;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京王府井");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.396117;
        latitude = 39.911109;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京国家大剧院");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.402764;
        latitude = 39.932718;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京景山公园");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.342542;
        latitude = 39.947322;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京动物园");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.316671;
        latitude = 39.999409;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京大学");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.333056;
        latitude = 40.010241;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("清华大学");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.396584;
        latitude = 40.02505;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("奥林匹克森林公园");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.215198;
        latitude = 40.008915;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("北京植物园");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.353771;
        latitude = 40.066031;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("育新地铁站");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);

        longitude = 116.295127;
        latitude = 40.102441;
        location = new Location();
        location.setUserId(1L);
        location.setAddress("昌平永旺商城");
        location.setCreated(System.currentTimeMillis());
        location.setUpdated(System.currentTimeMillis());
        location.setLastUpdated(System.currentTimeMillis());
        location.setLocation(new GeoJsonPoint(longitude, latitude));
        mongoTemplate.save(location);
    }

    /**
     * 修改地理位置
     */
    @Test
    public void testUpdateLocation() {
        double longitude = 116.403968;
        double latitude = 39.915094;
        //设置条件
        Query query = Query.query(Criteria.where("userId").is(1L));
        //更新
        Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                .set("updated", System.currentTimeMillis())
                .set("lastUpdated", System.currentTimeMillis());
        mongoTemplate.updateFirst(query, update, Location.class);
    }

    /**
     * 根据某个坐标中心点和半径查询附近的数据
     */
    @Test
    public void testQueryNear1() {
        //1、根据用户id，查询用户的位置信息
        Query query = Query.query(Criteria.where("userId").is(1L)); //北京天安门
        Location location = mongoTemplate.findOne(query, Location.class);
        if (location != null) {
            double distanceNum = 20;
            //2、已当前用户位置绘制原点
            GeoJsonPoint point = location.getLocation();
            //GeoJsonPoint point = new GeoJsonPoint(116.404, 39.915);
            //构造半径
            Distance distance = new Distance(distanceNum, Metrics.KILOMETERS);
            //画了一个圆圈
            Circle circle = new Circle(point, distance);
            //构造query对象
            Query localQuery = Query.query(Criteria.where("location").withinSphere(circle));
            //省略其他内容
            List<Location> list = mongoTemplate.find(localQuery, Location.class);
            list.forEach(System.err::println);
        }
    }

    /**
     * 根据某个坐标中心点和半径查询附近的数据和距离
     */
    @Test
    public void testQueryNear2() {
        //1、根据用户id，查询用户的位置信息
        Query query = Query.query(Criteria.where("userId").is(1L));
        Location location = mongoTemplate.findOne(query, Location.class); //北京天安门
        if (location != null) {
            double distance = 20;
            //1、构造中心点(圆点)
            GeoJsonPoint point = location.getLocation();
            //GeoJsonPoint point = new GeoJsonPoint(116.404, 39.915);
            //2、构建NearQuery对象
            NearQuery localQuery = NearQuery.near(point, Metrics.KILOMETERS).maxDistance(distance, Metrics.KILOMETERS);
            //3、调用mongoTemplate的geoNear方法查询
            GeoResults<Location> results = mongoTemplate.geoNear(localQuery, Location.class);
            //4、解析GeoResult对象，获取距离和数据
            for (GeoResult<Location> result : results) {
                Location locationResult = result.getContent();
                double value = result.getDistance().getValue();
                System.err.println(locationResult.getAddress() + "---距离：" + Math.round(value) + "km");
            }
        }
    }

    /**
     * 更新地理位置(新增或修改)
     *
     * @param userId
     * @param longitude
     * @param latitude
     * @param address
     * @return
     */
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //1、根据用户id查询位置信息
            Query query = Query.query(Criteria.where("userId").is(userId));
            Location location = mongoTemplate.findOne(query, Location.class);
            if (location == null) {
                //2、如果不存在用户位置信息，保存
                location = new Location();
                location.setUserId(userId);
                location.setAddress(address);
                location.setCreated(System.currentTimeMillis());
                location.setUpdated(System.currentTimeMillis());
                location.setLastUpdated(System.currentTimeMillis());
                location.setLocation(new GeoJsonPoint(longitude, latitude));
                mongoTemplate.save(location);
            } else {
                //3、如果存在，更新
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", location.getUpdated());
                mongoTemplate.updateFirst(query, update, Location.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Test
    public void testDebug1() {
        System.out.println("1111");
        System.out.println("2222");
        System.out.println("3333");
        System.out.println("4444");
        System.out.println("5555");
        MyUser myUser = new MyUser();
        myUser.setId(ObjectId.get());
        myUser.setUserId(ObjectId.get());
        System.err.println(myUser);

        this.testDebug2(myUser);
        System.err.println(myUser);
    }

    private void testDebug2(MyUser myUser) {
        myUser.setUserName("诸葛亮");
        myUser.setSex(1);
        myUser.setAge(24);
        this.testDebug3(100);
        myUser.setAddress(new Address("长沙市", "天心区"));
        Long id = idWorker.getNextId("test");
        System.err.println(id);
    }

    private void testDebug3(int count) {
        System.err.println(count);
        for (int i = 0; i < count; i++) {
            System.out.println("我是80");
            if(i == 80){
                System.err.println("我循环到数值："+ i +"了");
            }
        }
    }


}
