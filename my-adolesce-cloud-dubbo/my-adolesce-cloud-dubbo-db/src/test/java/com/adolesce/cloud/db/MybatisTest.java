package com.adolesce.cloud.db;

import com.adolesce.cloud.db.mapper.BatisUserAnnoMapper;
import com.adolesce.cloud.db.mapper.BatisUserXmlMapper;
import com.adolesce.cloud.dubbo.domain.db.BatisAddress;
import com.adolesce.cloud.dubbo.domain.db.BatisUser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Mybatis官方文档：https://mybatis.org/mybatis-3/zh/java-api.html
 *
 * Mybatis 原生方式使用集成步骤
 * 1、数据库环境准备：导入 my_batis_user.sql
 * 2、引入pom依赖：mysql-connector-java、mybatis-spring-boot-starter
 * 3、application文件配置spring.datasource 数据源
 * 4、启动类打上@MapperScan("com.adolesce.common.mapper")注解 指定Mapper接口扫描路径
 * 5、application配置文件配置mybatis扫描路径
 *      mybatis:
 *       mapper-locations: classpath:mapper/*Mapper.xml  #mapper文件映射文件路径
 *       type-aliases-package: com.adolesce.**.bo    #数据库对应实体类扫描
 * 6、如果是注解使用方式，编写注解Mapper类
 * 7、如果是xml使用方式，编写Mapper接口和mapper.xml文件
 * 8、测试使用
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisTest {
    //xml方式
    @Autowired(required = false)
    private BatisUserXmlMapper usersMapper;
    //注解方式
    @Autowired(required = false)
    private BatisUserAnnoMapper usersAnnoMapper;

    /**
     * 单条保存
     */
    @Test
    public void testInsert() {
        BatisUser user = new BatisUser();
        user.setAge(33);
        user.setPassword("1234");
        user.setUserName("caocao");
        user.setName("曹操");
        user.setBirthday(LocalDate.now());
        user.setEmail("3636@qq.com");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.usersAnnoMapper.insert(user);
        System.out.println(user);
    }

    /**
     * 批量保存
     */
    @Test
    public void testBatchInsert() {
        List<BatisUser> usersList = new ArrayList<>();
        BatisUser user;
        for (int i = 1; i <= 3; i++) {
            user = new BatisUser();
            user.setAge(31);
            user.setPassword("1234");
            user.setUserName("hanmeimei");
            user.setName("韩梅梅222");
            user.setBirthday(LocalDate.now());
            user.setEmail("3636@qq.com");
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            usersList.add(user);
        }
        this.usersMapper.batchInsert(usersList);
    }

    /**
     * 根据ID删除
     */
    @Test
    public void testDeleteById() {
        this.usersAnnoMapper.deleteById(10L);
    }

    /**
     * 根据ID集合批量删除
     */
    @Test
    public void testDeleteByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(10L);
        ids.add(11L);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", ids);
        this.usersMapper.deleteByIds(paramMap);
    }

    /**
     * 根据ID字符串进行删除
     */
    @Test
    public void testDeleteByIdsStr() {
        String idsStr = "10,11";
        this.usersAnnoMapper.deleteByIdsStr(idsStr,"123");
    }

    /**
     * 条件更新
     */
    @Test
    public void testUpdate() {
        BatisUser user = this.usersAnnoMapper.getById(9L);
        System.out.println(user.toString());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthday = LocalDate.parse("2018-01-12", dateTimeFormatter);
        user.setBirthday(birthday);
        user.setUserName("zhaoyun");
        user.setName("赵云");

        //将表中对应字段更新为null
        user.setAge(null);
        this.usersAnnoMapper.update(user);
    }

    /**
     * 根据ID查询
     */
    @Test
    public void testQueryOne() {
        BatisUser user = this.usersAnnoMapper.getById(1L);
        System.err.println(user);
    }

    /**
     * 条件查询
     */
    @Test
    public void testQueryByParam(){
        BatisUser user = new BatisUser();
        //user.setUserName("sunqi");
        //user.setBirthday(LocalDate.parse("2015-07-09"));
        user.setStartTime("2021-05-07 02:01:05");
        user.setEndTime("2021-05-09 02:01:02");

        List<BatisUser> list = this.usersMapper.queryByParam(user);
        list.stream().forEach(System.out::println);
        System.err.println(list);
    }

    /**
     * 分页条件查询
     */
    @Test
    public void testPageQueryByParam(){
        BatisUser user = new BatisUser();
        PageHelper.startPage(1, 3);    //开始页(从1开始)、查询条数
        PageHelper.orderBy("birthday desc,create_time desc");
        //PageHelper.startPage(1,10,"birthday desc");

        //isPageSizeZero 是否支持PageSize为0，true且pageSize=0时返回全部结果，false时分页,null时用默认配置
        //isCount 是否查询结果集总条数
        //Page<?> page = PageHelper.startPage(pageNum, pageSize, isCount, isReasonable, isPageSizeZero);

        /*PageInfo<BatisUser> list = new PageInfo(this.usersMapper.queryByParam(user));
        List<BatisUser> recordList = list.getList();*/

        Page<BatisUser> list = (Page<BatisUser>) this.usersMapper.queryByParam(user);
        List<BatisUser> recordList = list.getResult();

        recordList.stream().peek(record -> record.getBirthday()).forEach(System.out::println);
    }

    /**
     * 多表分页条件查询
     */
    @Test
    public void mutiTablePageQueryByParam(){
        BatisUser user = new BatisUser();
        PageHelper.startPage(1,10,"record.card_no desc");
        List<BatisUser> users = this.usersAnnoMapper.queryPageByName("赵晓雅");
        users.stream().forEach(System.err::println);
    }

    /**
     * 一对多查询（通过user级联查询出address）
     * 这种一对多查询方式需要注意两点：
     * A.用分页插件pageHelper的时候，该种方式会导致分页查询结果错乱（解决办法：使用第二种一对多方式：MybatisPlusTest.oneToMany()）。
     * B.如果几个表有字段名相同的情况，字段赋值可能被覆盖。我们可以给字段取别名的方式来解决，如下：
     */
    @Test
    public void oneToMany(){
        PageHelper.startPage(1,20,"u.birthday desc");
        Map<String,Object> params = new HashMap<>();
        params.put("id",2L);
        List<BatisUser> users = this.usersMapper.selectBatisUserByParams(params);
        users.stream().forEach(System.err::println);
    }

    /**
     * 通过address级联查询出所属user
     */
    @Test
    public void oneToOne(){
        Map<String,Object> params = new HashMap<>();
        params.put("id",1L);
        List<BatisAddress> addresses = this.usersMapper.selectBatisAddressByParams(null);
        addresses.stream().forEach(System.err::println);
    }
}