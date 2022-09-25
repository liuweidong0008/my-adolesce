package com.adolesce.cloud.db;

import com.adolesce.cloud.dubbo.api.db.MpUserApi;
import com.adolesce.cloud.dubbo.domain.db.MpAddress;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.cloud.dubbo.enums.SexEnum;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * mybatis_plus集成步骤
 * <p>
 * 1、数据库环境准备：导入 my_mp_user.sql
 * 2、引入pom依赖：mysql-connector-java、lombok、mybatis-plus-boot-starter
 * 3、application文件配置spring.datasource 数据源
 * 4、可选：application文件配置 mybatis-plus 参数：如主键自增、表前缀、打印sql日志
 * 5、集成代码生成：引入pom依赖  mybatis-plus-generator、velocity或freemarker
 * 6、自动生成代码，将代码拷贝至相应位置
 * 7、启动类增加  @MapperScan 注解，扫描mapper接口
 * 8、编写MybatisPlusPageConfig配置类，加载mybatis_plus分页插件（加载这个插件了分页才有效果）
 * 9、测试使用
 * <p>
 * 注意：2.0版本与3.0版本 API不一样
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusTest {
    @Autowired(required = false)
    private MpUserApi mpUserApi;

    /**
     *  一、单条插入不为null的字段
     *      this.mpUserApi.save(user);
     *
     *  二、批量插入字段不为null的字段
     *      this.mpUserApi.saveBatch(users);
     *
     *  三、批量插入字段不为null的字段,每插入2条进行一次flush
     *      this.mpUserApi.saveBatch(users,2);
     *
     *  四：先根据ID进行update，成功则返回，如update不成功，再insert
     *      this.mpUserApi.saveOrUpdate(user);
     *
     *  五：先根据Wrapper条件进行update，成功则返回，如update不成功，再insert
     *      this.mpUserApi.saveOrUpdate(user,updateWrapper);
     *
     *  六、先根据ID进行update，成功则返回，如update不成功，再insert
     *      this.mpUserApi.saveOrUpdateBatch(users);
     *
     *  七、先根据ID进行update，成功则返回，如update不成功，再insert,每插入或者更新2条刷新一次
     *      this.mpUserApi.saveOrUpdateBatch(users,2);
     */
    @Test
    public void testInsert() {
        MpUser user = new MpUser();
        user.setUserName("itcast5");
        user.setPassword("itheima5");
        user.setName("赵云5");
        user.setAge(99);
        user.setSex(SexEnum.WOMAN);
        user.setPhone("18301327332");
        user.setBirthday(LocalDate.now());
        boolean result = mpUserApi.save(user);
        System.err.println(result);
    }

    /**
     *  一、根据主键进行删除
     *     this.mpUserApi.removeById(id);
     *
     *  二、根据主键集合进行删除
     *     this.mpUserApi.removeByIds(ids);
     *
     *  三、根据map进行删除
     *     this.mpUserApi.removeByMap(map);
     *
     *  四、根据wrapper条件进行删除
     *     this.mpUserApi.remove(wrapper);
     */
    @Test
    public void testDelete() {
        //1. 根据id删除
        //boolean result = mpUserApi.removeById(6L);

        //2. 根据id集合批量删除
        List ids = new ArrayList();
        ids.add(6);
        ids.add(7);
        //boolean result = mpUserApi.removeByIds(ids);

        //3. 根据map构造条件，删除
        //delete from my_mp_user where user_name = ? and age = ?
        Map<String, Object> map = new HashMap<>();
        map.put("user_name", "zhangsan");
        map.put("age", "18");
        //boolean result = mpUserApi.removeByMap(map);

        //4. 根据Wrapper构造条件，删除
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.isNull("password");
        queryWrapper.or();
        queryWrapper.eq("password","123");

        boolean result = this.mpUserApi.remove(queryWrapper);
        System.err.println(result);
    }

    /**
     * 一、根据主键进行更新
     *     this.mpUserApi.updateById(user);
     *
     * 二、根据主键进行批量更新
     *     this.mpUserApi.updateBatchById(users);
     *
     * 三、根据主键进行批量更新,每更新2条进行一次flush，默认每更新30条flush
     *     this.mpUserApi.updateBatchById(users,2);
     *
     * 四、根据wapper更新
     *     this. mpUserApi.update(wrapper);
     *
     * 五、根据user更新参数对符合wrapper条件的数据进行更新，注意：不会更新主键以及为属性为null的字段
     *     this.mpUserApi.update(user,wrapper);
     */
    @Test
    public void testUpdateById() {
        //1.根据主键进行更新
        MpUser user = new MpUser();
        user.setId(6L);
        user.setPassword("123456");
        //boolean result = mpUserApi.updateById(user);

        //2.根据主键进行批量更新
        List<MpUser> myUsers = new ArrayList<>();
        MpUser user1 = new MpUser();
        user1.setId(6L);
        user1.setPassword("123");

        MpUser user2 = new MpUser();
        user2.setId(5L);
        user2.setPassword("456");

        myUsers.add(user1);
        myUsers.add(user2);
        //boolean result = mpUserApi.updateBatchById(myUsers);

        //3、根据Wrapper进行更新（通过set指定要设置的字段）
        UpdateWrapper<MpUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("id","21").set("user_name","hello").set("password",null);
        //boolean result = mpUserApi.update(wrapper);

        //4、根据Entity和Wrapper进行更新（通过Entity指定要设置的字段）
        QueryWrapper qwrapper = new QueryWrapper<>();
        qwrapper.eq("name","赵云3");
        user = new MpUser();
        user.setPassword("22");
        user.setAge(22);
        user.setBirthday(null);
        boolean result = this.mpUserApi.update(user, qwrapper);
        System.err.println(result);
    }

    /**
     * 测试允许将字段更新为null
     */
    @Test
    public void testUpdateAllowNull() {
        //方式一：全局设置【慎用】 （ global-config:db-config:update-strategy: IGNORED）
        //方式二：在允许修改为null的字段上添加注解  @TableField(updateStrategy = FieldStrategy.IGNORED)
        /*QueryWrapper qwrapper = new QueryWrapper<>();
        qwrapper.eq("name","赵云5");

        MpUser user = new MpUser();
        user.setPassword("22");
        user.setAge(null);
        boolean result = this.mpUserApi.update(user, qwrapper);*/

        //方式三、通过UpdateWrapper
       LambdaUpdateWrapper<MpUser> updateWrapper =
                Wrappers.<MpUser>lambdaUpdate()
                    .set(MpUser::getAge,null)
                    .set(MpUser::getPassword,"888")
                    .eq(MpUser::getName,"赵云5");

        boolean result = this.mpUserApi.update(updateWrapper);
        System.err.println(result);
    }


    /**
     * 根据id查询
     */
    @Test
    public void testSelectById() {
        MpUser user = mpUserApi.getById(1L);
        System.err.println(user);
    }

    /**
     * 分页查询：
     * 1. 当前页码：currentPage
     * 2. 每页显示条数：size
     *
     * 注意：使用mp的分页要设置一个拦截器！！！
     */
    @Test
    public void testSelectPage() {
        int current = 1;//当前页码
        int size = 2;//每页显示条数
        IPage<MpUser> page = new Page(current, size);
        mpUserApi.page(page);

        List<MpUser> records = page.getRecords();//当前页的数据
        long pages = page.getPages();//总页数
        long total = page.getTotal();//总记录数

        System.err.println(records);
        System.err.println(pages);
        System.err.println(total);
    }

    /**
     * 基础比较查询
     *
     * Wrapper:
     * 1.QueryWrapper
     *      LambdaQueryWrapper
     * 2.UpdateWrapper
     *      LambdaUpdateWrapper
     */
    @Test
    public void testWrapper1() {
        //1.创建查询条件构建器
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        //2.设置条件
        wrapper.eq("user_name", "lisi")
                .lt("age", 23)
                .in("name", "李四", "王五");
        //select * from my_mp_user where user_name = ? and age < ? and name in (?,?)
        this.list(wrapper);

        LambdaQueryWrapper lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.clear();
    }

    @Test
    public void testWrapper2() {
        //1.创建查询条件构建器
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        //2.设置条件
        wrapper.eq("user_name", "lisi")
                .or()   //只对下一个条件生效
                .lt("age", 23)
                .in("name", "李四", "王五");
        //select * from my_mp_user where user_name = ? or age < ? and name in (?,?)
        this.list(wrapper);
    }

    /**
     * 模糊查询
     */
    @Test
    public void testWrapper3() {
        //1.创建查询条件构建器
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        //2.设置条件
        //wrapper.likeLeft("user_name","zhang");
        //select * from my_mp_user where user_name like '%zhang'

        //wrapper.likeRight("user_name","zhang");
        //select * from my_mp_user where user_name like 'zhang%'

        wrapper.like("user_name", "zhang");
        //select * from my_mp_user where user_name like '%zhang%'

        this.list(wrapper);
    }

    /**
     * 排序查询
     */
    @Test
    public void testWrapper4() {
        //1.创建查询条件构建器
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        //2.设置条件
        wrapper.eq("user_name", "lisi")
                .or()
                .lt("age", 23)
                .in("name", "李四", "王五")
                //.orderBy(true,true,"age")
                .orderByDesc("age");

        //select * from my_mp_user where user_name = ? or age < ? and name in (?,?) order by age desc
        this.list(wrapper);
    }

    /**
     * select：指定需要查询的字段
     */
    @Test
    public void testWrapper5() {
        //1.创建查询条件构建器
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        //2.设置条件
        wrapper.eq("user_name", "lisi")
                .or()
                .lt("age", 23)
                .in("name", "李四", "王五")
                //.orderBy(true,true,"age")
                .orderByDesc("age")
                .select("count(distinct user_name) as u_count");

        //SELECT id,user_name,password,name,sex,birthday FROM my_mp_user WHERE (user_name = ? OR age < ? AND name IN (?,?)) ORDER BY age DESC
        this.list(wrapper);
    }

    /**
     * 分页条件查询
     */
    @Test
    public void testWrapper6() {
        int current = 3;//当前页码
        int size = 2;//每页显示条数
        //1. 构建分页对象
        Page<MpUser> page = new Page<>(current, size);
        //2. 构建条件对象
        QueryWrapper<MpUser> wrapper = new QueryWrapper();
        wrapper.lt("age", 99);

        IPage pageResult = mpUserApi.page(page, wrapper);

        List<MpUser> records = pageResult.getRecords();
        long total = page.getTotal();
        long pages = page.getPages();

        //SELECT id,user_name,password,name,age,email FROM my_mp_user WHERE (age < ?) LIMIT ?,?
        System.err.println(records);
        System.err.println(total);
        System.err.println(pages);
    }

    /**
     * LambdaQueryWrapper：消除代码中的硬编码
     */
    @Test
    public void testWrapper7() {
        LambdaQueryWrapper<MpUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MpUser::getUserName, "zhangsan");
        MpUser user = this.mpUserApi.getOne(wrapper);
        System.err.println(user);
    }

    /**
     * 不等于查询
     */
    @Test
    public void testWrapper8() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.ne("user_name", "lisi");
        //WHERE user_name <> ?
        this.list(wrapper);
    }

    /**
     * 是否为空查询
     * isNull
     * is not null
     */
    @Test
    public void testWrapper9() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.isNull("user_name");
        //WHERE (user_name IS NULL)

        //wrapper.isNotNull("user_name");
        //WHERE (user_name IS NOT NULL)
        this.list(wrapper);
    }

    /**
     * 比较、范围查询
     * between  xx <= xx <= xx		notBetween
     * gt lt    xx < xx < xx
     * ge le    xx <= xx <= xx
     */
    @Test
    public void testBetween() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = LocalDate.parse("2007-02-19", formatter);
        LocalDate endTime = LocalDate.parse("2013-02-08", formatter);

        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.between("birthday", startTime, endTime);
        // WHERE (transfer_time BETWEEN ? AND ?)     2007-02-19(LocalDate), 2013-02-08(LocalDate)
        this.list(wrapper);
    }

    @Test
    public void testGtLt() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse("2021-05-04 02:00:55", format);
        LocalDateTime endTime = LocalDateTime.parse("2021-05-05 02:00:54", format);

        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.ge("create_time", startTime)
                .le("create_time", endTime)
                .orderByDesc("create_time");

        //WHERE (create_time > ? AND create_time < ?) 	2021-05-04T02:00:56(LocalDateTime), 2021-05-05T02:00:53(LocalDateTime)
        this.list(wrapper);
    }

    //注意：由于实体类中时间类型为LocalDateTime，而这里查询用的时间类型用的Date，查询出的结果会出现不准确的情况，应该用LocalDateTime去构建查询条件
    @Test
    public void testGeLe() throws ParseException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse("2021-05-04 02:00:56", format);
        LocalDateTime endTime = LocalDateTime.parse("2021-05-07 02:00:59", format);

        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.ge("create_time", startTime);
        wrapper.le("create_time", endTime);

        //WHERE (create_time >= ? AND create_time <= ?)  	2021-05-04T02:00:56(LocalDateTime), 2021-05-05T02:00:53(LocalDateTime)
        this.list(wrapper);
    }

    /**
     * 返回Map类型结果集
     */
    @Test
    public void testSelectMaps() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.eq("age", "20");
        List<Map<String, Object>> list = this.mpUserApi.listMaps(wrapper);
        //WHERE (age = ?)
        for (Map<String, Object> map : list) {
            System.err.println(map);
        }
    }

    /**
     * map封装条件进行查询
     */
    @Test
    public void testSelectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", "20");
        List<MpUser> record = this.mpUserApi.listByMap(map);
        System.err.println(record);
    }

    /**
     * 通过ID集合查询
     */
    @Test
    public void testSelectBatchIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ids.add(i + "");
        }
        List<MpUser> record = this.mpUserApi.listByIds(ids);
        System.err.println(record);
    }

    /**
     * 查询结果集条数
     */
    @Test
    public void testSelectCount() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        wrapper.eq("age", "20");
        int count = this.mpUserApi.count(wrapper);
        System.err.println(count);

        //SELECT COUNT(1) FROM my_mp_user WHERE (age = ? )
    }

    /**
     * groupBy 分组查询
     */
    @Test
    public void testGroupBy() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        wrapper.select("age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage")
                .in("user_name", "zhangsan", "lisi", "wangwu", "zhaoliu", "sunqi", "itcast")
                .groupBy("age");
        //SELECT age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage FROM my_mp_user WHERE (user_name IN (?,?,?,?,?,?)) GROUP BY age

        //.groupBy("user_name");    //等价于 wrapper.groupBy("age,user_name");
        this.list(wrapper);

        //listMaps方法查询
        List<Map<String, Object>> maps = this.mpUserApi.listMaps(wrapper);
        System.out.println(maps);
    }

    /**
     * having 分组条件查询
     */
    @Test
    public void testHaving() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        wrapper.select("age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage")
                .in("user_name", "zhangsan", "lisi", "wangwu", "zhaoliu", "sunqi", "itcast")
                .groupBy("age");
        wrapper.having("COUNT(1)>1 and age != 28");
        wrapper.having("age != {0}", "24");

        this.list(wrapper);
        //SELECT age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage FROM my_mp_user WHERE (user_name IN (?,?,?,?,?,?))
        // GROUP BY age HAVING COUNT(1)>1 and age != '24' AND age != ?
    }

    /**
     * Mybatis-Plus 集成PageHelper分页插件
     * 1、引入pom依赖：pagehelper-spring-boot-starter
     * 2、在查询前设置分页参数
     * 3、用PageInfo转换查询结果集
     */
    @Test
    public void testSelectByPageHelper() {
        //Mybatis-Plus Wrapper构建查询条件
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.orderByDesc("create_time");

        //一、PageHelper分页
        //PageHelper构建分页参数
        PageHelper.startPage(1, 2);    //查询第一页、一页两条数据
        PageHelper.orderBy("birthday desc");  //与wrapper同时设置排序，PageHper生效
        //PageHelper.startPage(1,10,"birthday desc");

        List<MpUser> list = this.mpUserApi.list(wrapper);
        //PageInfo<MpUser> pageInfo = new PageInfo(list);
        //List<MpUser> resultList = pageInfo.getList();

        com.github.pagehelper.Page<MpUser> page = (com.github.pagehelper.Page<MpUser>) list;
        List<MpUser> resultList = page.getResult();

        for (MpUser record : resultList) {
            System.err.println(record);
        }


        //二、Mybatis_plus分页查询
        Page<MpUser> mpage = new Page(1, 2);
        this.mpUserApi.page(mpage, wrapper);

        List<MpUser> pageList = mpage.getRecords();
        for (MpUser record : pageList) {
            System.err.println(record);
        }
        long pages = page.getPages();//总页数
        long total = page.getTotal();//总记录数
    }

    /**
     * 自定义查询（根据姓名多表查询结果集）
     */
    @Test
    public void testSelectByCustom() {
        List<MpUser> list = this.mpUserApi.queryByNameCustom("赵晓雅");
        for (MpUser record : list) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
    }

    /**
     * 自定义分页查询（根据姓名多表分页查询结果集）
     * mybatis-plus自定义分页查询时，查询参数为map和对象都可以，但是xml中获取属性必须以【参数名.属性】的方式取值
     */
    @Test
    public void testSelectPageyCustom() {
        IPage<MpUser> page = new Page<>(2, 3);
        this.mpUserApi.queryPageByNameCustom(page, "赵晓雅");
        for (MpUser record : page.getRecords()) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
    }

    private void list(Wrapper<MpUser> recordQueryWrapper) {
        List<MpUser> records = this.mpUserApi.list(recordQueryWrapper);
        for (MpUser record : records) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
    }

    /**
     * 一对多查询（通过user级联查询出address）
     */
    @Test
    public void oneToMany(){
        PageHelper.startPage(1,5,"u.birthday desc");
        Map<String,Object> params = new HashMap<>();
        params.put("id",2L);
        List<MpUser> users = this.mpUserApi.selectMpUserByParams(null);
        users.stream().forEach(System.err::println);
    }

    /**
     * 通过address级联查询出所属user
     */
    @Test
    public void manyToOne(){
        Map<String,Object> params = new HashMap<>();
        params.put("id",1L);
        List<MpAddress> addresses = this.mpUserApi.selectMpAddressByParams(null);
        addresses.stream().forEach(System.err::println);
    }

    /**
     基本比较查询：
     eq() :等于 =
     ne() :不等于<>
     gt() :大于 >
     ge() :大于等于 >=
     lt() :小于<
     le() :小于等于 <=
     between() : BETWEEN 值1 AND 值2
     notBetween() : NOT BETWEEN 值1 AND 值2
     in() :in
     notIn() ： not in

     逻辑查询
     or( ) ：让紧接着下一个方法用or连接

     模糊查询：
     like
     notLike
     likeLeft
     likeRight

     排序查询
     orderBy
     orderByAsc
     orderByDesc
     */
}
