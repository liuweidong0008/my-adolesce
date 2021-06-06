package com.adolesce.server;

import com.adolesce.common.bo.Address;
import com.adolesce.common.bo.MyUser;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
 * db.createUser({ user: 'root', pwd: 'root', roles: [ { role: "root", db: "admin" } ] })
 * 授权进入
 * mongo -u "root" -p "root" --authenticationDatabase "admin"
 * 创建普通用户
 * db.createUser({ user: 'lwd', pwd: 'lwd', roles: [ { role: "readWrite", db: "adolesce" } ] });
 * 授权进入
 * mongo -u "lwd" -p "lwd" --authenticationDatabase "admin"
 *
 *
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

    /**
     * 插入数据
     */
    @Test
    public void testSaveMyUser() {
        MyUser myUser = new MyUser();
        myUser.setId(ObjectId.get());
        myUser.setUserId(ObjectId.get());
        myUser.setUserName("张三");
        myUser.setSex(1);
        myUser.setAge(35);
        myUser.setAddress(new Address("北京市", "三叉路"));
        mongoTemplate.save(myUser);
    }

    /**
     * 删除用户表数据
     */
    @Test
    public void testRemoveMyUser() {
        Query query = Query.query(Criteria.where("age").is(18)
                .and("sex").is(1)
        );
        DeleteResult deleteResult = this.mongoTemplate.remove(query, MyUser.class);
        System.err.println(deleteResult.getDeletedCount() > 0);
    }

    /**
     * 更新
     */
    @Test
    public void testUpdateMyUser() {
        Query query = Query.query(Criteria.where("userName").is("刘威东"));
        Update update = Update.update("age", 41).set("sex", 2).set("isOld", true)
                //.addToSet("phone","18301327332").addToSet("hobby").value("篮球") //添加集合类型字段
                .unset("phone");//删除phone字段
        UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, MyUser.class);
    }

    /**
     * 查询数量
     */
    @Test
    public void testQueryMyUserCount() {
        Query query = Query.query(Criteria.where("age").is(18));
        long count = this.mongoTemplate.count(query, MyUser.class);
        System.err.println(count);
    }

    /**
     * 条件查询
     */
    @Test
    public void testQueryMyUsersByName() {
        Query query = Query.query(Criteria.where("userName").is("刘威东"))
                .with(Sort.by(Sort.Order.desc("age"))).limit(1);
        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
    }

    /**
     * 逻辑查询
     */
    @Test
    public void testQueryOrMyUsers() {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("age").is(35),Criteria.where("userName").is("刘威东"));
        Query query = Query.query(criteria);
        //where age = 35 or userName = '刘威东'
        query = Query.query(Criteria.where("userName").is("刘威东").orOperator(Criteria.where("age").is(35),Criteria.where("userName").is("刘威东")));
        //where userName = '刘威东' and (age = 35 or userName = '刘威东')
        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
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
        MyUser myUser = this.mongoTemplate.findById(new ObjectId("60ae770fdb54ec2109beb0b8"), MyUser.class);
        System.err.println(myUser);

        Query query = Query.query(Criteria.where("_id").is("60ae770fdb54ec2109beb0b8"));
        myUser = this.mongoTemplate.findOne(query, MyUser.class);
        System.err.println(myUser);

        query = Query.query(Criteria.where("id").is("60ae770fdb54ec2109beb0b8"));
        myUser = this.mongoTemplate.findOne(query, MyUser.class);
        System.err.println(myUser);
    }

    @Test
    public void testLikeQueryMyUser() {
        //完全匹配
        Pattern pattern1 = Pattern.compile("^刘威$", Pattern.CASE_INSENSITIVE);
        Query query = Query.query(Criteria.where("userName").regex(pattern1));
        List<MyUser>  myUsers = mongoTemplate.find(query, MyUser.class);
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
     * 分页查询
     */
    @Test
    public void testQueryMyUserPageList() {
        Integer pageNum = 1;//查询第几页
        Integer pageSize = 2;//一页多少条数据
        Query query = new Query().limit(pageSize).skip((pageNum - 1) * pageSize);
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
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("score")));

        //查询参数
        Query query = Query.query(Criteria.where("userName").is("刘威东")).with(pageRequest);

        List<MyUser> users = this.mongoTemplate.find(query, MyUser.class);
        System.err.println(users);
    }

    /**
     * 查询所有
     */
    @Test
    public void testFindMyUserAll() {
        List<MyUser> users = this.mongoTemplate.findAll(MyUser.class);
        System.err.println(users);
    }


    @Test
    public void serize() {
        MyUser user = new MyUser();
        user.setAge(11);
        user.setSex(1);
        user.setUserName("lwd");
        ResponseEntity<MyUser> response = ResponseEntity.ok(user);

    }

}
