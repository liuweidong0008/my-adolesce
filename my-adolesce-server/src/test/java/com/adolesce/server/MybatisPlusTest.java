package com.adolesce.server;

import com.adolesce.common.bo.MpUser;
import com.adolesce.common.enums.SexEnum;
import com.adolesce.server.service.IMpUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * mybatis_plus集成步骤
 *
 * 1、数据库环境准备：导入 my_mp_user.sql
 * 2、引入pom依赖：mysql-connector-java、lombok、mybatis-plus-boot-starter
 * 3、application文件配置spring.datasource 数据源
 * 4、可选：application文件配置 mybatis-plus 参数：如主键自增、表前缀、打印sql日志
 * 5、集成代码生成：引入pom依赖  mybatis-plus-generator、velocity
 * 6、自动生成代码，将代码拷贝至相应位置
 * 7、启动类增加  @MapperScan 注解，扫描mapper接口
 * 8、编写MybatisPlusPageConfig配置类，加载mybatis_plus分页插件（加载这个插件了分页才有效果）
 * 9、测试使用
 *
 * 注意：2.0版本与3.0版本 API不一样
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusTest {
    @Autowired
    private IMpUserService mpUserService;

    /**
     * 根据id查询
     */
    @Test
    public void testSelectById() {
        MpUser user = mpUserService.getById(1L);
        System.err.println(user);
    }


    /**
     * 添加
     */
    @Test
    public void testIsnert() {
        MpUser user = new MpUser();
        user.setUserName("itcast");
        user.setPassword("itheima");
        user.setSex(SexEnum.WOMAN);

        boolean result = mpUserService.save(user);
        System.err.println(result);
    }

    /**
     * 删除
     */
    @Test
    public void testDelete() {
        //1. 根据id删除
        //boolean result = mpUserService.removeById(6L);

        //2. 根据id集合批量删除
        List ids = new ArrayList();
        ids.add(6);
        ids.add(7);
        //boolean result = mpUserService.removeByIds(ids);

        //3. 根据map构造条件，删除
        Map<String, Object> map = new HashMap<>();
        //delete from my_mp_user where user_name = ? and age = ?
        map.put("user_name", "zhangsan");
        map.put("age", "18");
        boolean result = mpUserService.removeByMap(map);

        System.err.println(result);
    }

    /**
     * 修改
     */
    @Test
    public void testUpdateById() {
        MpUser user = new MpUser();
        user.setId(6L);
        user.setPassword("1111111");
        boolean result = mpUserService.updateById(user);
        System.err.println(result);
    }

    /**
     * 分页查询：
     * 1. 当前页码：currentPage
     * 2. 每页显示条数：size
     * <p>
     * 注意：使用mp的分页要设置一个拦截器！！！
     */
    @Test
    public void testSelectPage() {
        int current = 1;//当前页码
        int size = 2;//每页显示条数
        IPage<MpUser> page = new Page(current, size);
        mpUserService.page(page);

        List<MpUser> records = page.getRecords();//当前页的数据
        long pages = page.getPages();//总页数
        long total = page.getTotal();//总记录数

        System.err.println(records);
        System.err.println(pages);
        System.err.println(total);
    }

    /**
     * 基础比较查询
     * <p>
     * Wrapper:
     * 1.QueryWrapper
     * LambdaQueryWrapper
     * 2.UpdateWrapper
     * LambdaUpdateWrapper
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
                .select("id", "user_name");

        //select id,user_name from my_mp_user where user_name = ? or age < ? and name in (?,?) order by age asc
        this.list(wrapper);
    }

    /**
     * 分页条件查询
     */

    @Test
    public void testWrapper6() {
        int current = 2;//当前页码
        int size = 2;//每页显示条数
        //1. 构建分页对象
        Page<MpUser> page = new Page<>(current, size);
        //2. 构建条件对象
        QueryWrapper<MpUser> wrapper = new QueryWrapper();
        wrapper.lt("age", 23);

        IPage pageResult = mpUserService.page(page, wrapper);

        List<MpUser> records = pageResult.getRecords();
        long total = page.getTotal();
        long pages = page.getPages();

        //SELECT id,user_name,password,name,age,email FROM my_mp_user WHERE (age < ?) LIMIT ?,?         23  2  2
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
        MpUser user = this.mpUserService.getOne(wrapper);
        System.err.println(user);
    }


    /**
     * 删除条件
     */

    @Test
    public void testWrapper8() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", "bbb");
        this.mpUserService.remove(wrapper);
    }

    /**
     * 修改条件
     */

    @Test
    public void testWrapper9() {
        UpdateWrapper<MpUser> wrapper = new UpdateWrapper<>();
        //条件
        wrapper.eq("user_name", "lisi")
                .set("password", "22222");
        //update my_mp_user set password = ? where user_name = ?
        this.mpUserService.update(null, wrapper);
    }

    /**
     * 对象封装条件进行修改
     */
    @Test
    public void testWrapper10() {
        UpdateWrapper<MpUser> wrapper = new UpdateWrapper<>();
        //条件
        wrapper.eq("user_name", "lisi");
        //update my_mp_user set password = ?,age = ? where user_name = ?
        MpUser user = new MpUser();
        user.setPassword("3333");
        user.setAge(33);
        this.mpUserService.update(user, wrapper);
    }

    /**
     * 不等于查询
     */
    @Test
    public void testWrapper11() {
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
    public void testWrapper12() {
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
        LocalDateTime startTime = LocalDateTime.parse("2021-05-04 02:00:20", format);
        LocalDateTime endTime = LocalDateTime.parse("2021-05-05 02:00:58", format);

        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.gt("create_time", startTime)
                .lt("create_time", endTime);

        //WHERE (transfer_time > ? AND transfer_time < ?) 	2021-05-04T02:00:20(LocalDateTime), 2021-05-05T02:00:58(LocalDateTime)
        this.list(wrapper);
    }

    //注意：由于实体类中时间类型为LocalDateTime，而这里查询用的时间类型用的Date，查询出的结果会出现不准确的情况，应该用LocalDateTime去构建查询条件
    @Test
    public void testGeLe() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = format.parse("2021-05-04 02:00:2");
        Date endTime = format.parse("2021-05-05 02:00:58");

        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.ge("create_time", startTime);
        wrapper.le("create_time", endTime);

        //WHERE (transfer_time >= ? AND transfer_time <= ?)  	2021-05-04T02:00:20(LocalDateTime), 2021-05-05T02:00:58(LocalDateTime)
        this.list(wrapper);
    }


    /**
     * 返回Map类型结果集
     */
    @Test
    public void testSelectMaps() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.eq("age", "20");
        List<Map<String, Object>> list = this.mpUserService.listMaps(wrapper);
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
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("age", "20");
        //map.put("", value)
        List<MpUser> record = this.mpUserService.listByMap(map);
        System.err.println(record);
    }

    /**
     * 通过ID集合查询
     */
    @Test
    public void testSelectBatchIds() {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            ids.add(i + "");
        }
        List<MpUser> record = this.mpUserService.listByIds(ids);
        System.err.println(record);
    }

    /**
     * 查询结果集条数
     */
    @Test
    public void testSelectCount() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.eq("age", "20");
        int count = this.mpUserService.count(wrapper);
        System.err.println(count);
        //SELECT COUNT(1) FROM my_mp_user WHERE (age = ? )
    }

    /**
     * groupBy 分组查询
     */
    @Test
    public void testGroupBy() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.select("age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage")
                .in("user_name", "zhangsan", "lisi", "wangwu", "zhaoliu", "sunqi", "itcast")
                .groupBy("age");
        //SELECT age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage FROM my_mp_user WHERE (user_name IN (?,?,?,?,?,?)) GROUP BY age

        // .groupBy("user_name");    //等价于 wrapper.groupBy("age,user_name");
        this.list(wrapper);

        //listMaps方法查询
        List<Map<String, Object>> maps = this.mpUserService.listMaps(wrapper);
        System.out.println(maps);
    }

    /**
     * having 分组条件查询
     */
    @Test
    public void testHaving() {
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.select("age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage")
                .in("user_name", "zhangsan", "lisi", "wangwu", "zhaoliu", "sunqi", "itcast")
                .groupBy("age");
        wrapper.having("COUNT(1)>1 and age != 28");
        wrapper.having("age != {0}", "24");
        //SELECT age,count(1) as count,max(birthday) as maxbirthday,sum(age) as totalage FROM my_mp_user WHERE (user_name IN (?,?,?,?,?,?)) GROUP BY age HAVING COUNT(1)>1 and age != '28' AND age != ?
    }

    /**
     * 集成PageHelper分页插件
     * 1、引入pom依赖：pagehelper-spring-boot-starter
     * 2、在查询前设置分页参数
     * 3、用PageInfo转换查询结果集
     */
    @Test
    public void testSelectByPageHelper() {
        //构建查询条件
        QueryWrapper<MpUser> wrapper = new QueryWrapper<MpUser>();
        wrapper.orderByDesc("create_time");
        //PageHelper分页查询
        //设置分页参数
        PageHelper.startPage(1, 2);    //查询第一页、一页两条数据
        PageHelper.orderBy("birthday desc");  //与wrapper同时设置排序，PageHper生效
        //PageHelper.startPage(1,10,"birthday desc");
        List<MpUser> list = this.mpUserService.list(wrapper);

        PageInfo<MpUser> pageInfo = new PageInfo(list);
        List<MpUser> pageInfoList = pageInfo.getList();
        for (MpUser record : pageInfoList) {
            System.err.println(record);
        }

        //mybatis_plus分页查询
        Page page = new Page<>(1, 3);
        this.mpUserService.page(page, wrapper);

        List<MpUser> pageList = page.getRecords();
        for (MpUser record : pageList) {
            System.err.println(record);
        }
    }

    /**
     * 自定义查询
     */
    @Test
    public void testSelectByCustom() {
        List<MpUser> list = this.mpUserService.queryByNameCustom("赵晓雅");
        for (MpUser record : list) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
    }

    /**
     * 自定义分页查询
     */
    @Test
    public void testSelectPageyCustom() {
        Page page = new Page<>(1, 2);
        IPage<MpUser> pateList = this.mpUserService.queryPageByNameCustom(page, "赵晓雅");
        for (MpUser record : pateList.getRecords()) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
    }


    private void list(Wrapper<MpUser> recordQueryWrapper) {
        List<MpUser> records = this.mpUserService.list(recordQueryWrapper);
        for (MpUser record : records) {
            System.err.println("------------printResult---------");
            System.err.println(record);
        }
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


    //一、单条插入不为null的字段
    //this.mpUserService.save(user);
    //INSERT INTO user ( id, age, birth_day, email, flag, hobby, `name`, `password`, salt, sex ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )

    //二、批量插入字段不为null的字段
    //this.userServiceImpl.saveBatch(users);

    //三、批量插入字段不为null的字段,每插入2条进行一次flush
    //this.mpUserService.saveBatch(users,2);

    //四：先update，成功则返回，如update不成功，再insert(老数据直接update，新数据先update，再insert)
    //this.mpUserService.saveOrUpdate(user);

    //五、先update，成功则返回，如update不成功，再insert(老数据直接update，新数据先update，再insert)
    //this.mpUserService.saveOrUpdateBatch(users);

    //六、根据wapper更新
    //this. mpUserService.update(wrapper);

    //七、根据user更新参数对符合wrapper条件的数据进行更新，注意：不会更新主键以及为属性为null的字段
    //this.mpUserService.update(user,wrapper);

    //八、根据主键进行更新
    //this.mpUserService.updateById(user);

    //九、根据主键进行批量更新
    //this.mpUserService.updateBatchById(users);

    //十、根据主键进行批量更新,每更新2条进行一次flush，默认每更新30条flush
    //this.mpUserService.updateBatchById(users,2);

    //十一、根据主键进行删除
    //this.mpUserService.removeById(id);

    //十二、根据wrapper条件进行删除
    //this.mpUserService.remove(wrapper);

    //十三、根据map进行删除
    //this.mpUserService.removeByMap(map);

    //十四、根据主键集合进行删除
    //this.mpUserService.removeByIds(ids);
}
