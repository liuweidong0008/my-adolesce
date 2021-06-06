package com.adolesce.server;

import com.adolesce.common.bo.BatisUser;
import com.adolesce.common.mapper.BatisUserAnnoMapper;
import com.adolesce.common.mapper.BatisUserXmlMapper;
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
 * Mybatis官方文档：https://mybatis.org/mybatis-3/zh/java-api.html
 *
 * Mybatis 原生方式使用 集成步骤
 * 1、数据库环境准备：导入 my_batis_user.sql
 * 2、引入pom依赖：mysql-connector-java、mybatis-spring-boot-starter
 * 3、application文件配置spring.datasource 数据源
 * 4、Application打上@MapperScan("com.adolesce.common.mapper")注解 指定Mapper接口扫描路径
 * 5、启动类增加  @MapperScan 注解，扫描mapper接口
 * 6、application配置文件配置mybatis扫描路径
 *      mybatis:
 *          mapper-locations: classpath:mapper/*Mapper.xml  #mapper文件映射文件路径
 *          type-aliases-package: com.adolesce.**.bo    #数据库对应实体类扫描
 * 7、如果是注解使用方式，编写注解Mapper类
 * 8、如果是xml使用方式，编写Mapper接口和mapper.xml文件
 * 9、测试使用
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisTest {
    //xml方式
    @Autowired
    private BatisUserXmlMapper usersMapper;
    //注解方式
    @Autowired
    private BatisUserAnnoMapper usersAnnoMapper;

    @Test
    public void testInsert() {
        BatisUser user = new BatisUser();
        user.setAge(31);
        user.setPassword("1234");
        user.setUserName("hanmeimei");
        user.setName("韩梅梅");
        user.setBirthday(LocalDate.now());
        user.setEmail("3636@qq/com");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.usersAnnoMapper.insert(user);
    }

    @Test
    public void testBatchInsert() {
        List<BatisUser> usersList = new ArrayList<>();
        BatisUser user;
        for (int i = 1; i <= 3; i++) {
            user = new BatisUser();
            user.setAge(31);
            user.setPassword("1234");
            user.setUserName("hanmeimei");
            user.setName("韩梅梅");
            user.setBirthday(LocalDate.now());
            user.setEmail("3636@qq/com");
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            usersList.add(user);
        }
        this.usersMapper.batchInsert(usersList);
    }

    @Test
    public void testDeleteById() {
        this.usersMapper.deleteById(19L);
    }

    @Test
    public void testDeleteByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(13L);
        ids.add(14L);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", ids);
        this.usersMapper.deleteByIds(paramMap);
    }

    @Test
    public void testDeleteByIdsStr() {
        String idsStr = "7,8,9,10";
        this.usersMapper.deleteByIdsStr(idsStr);
    }

    @Test
    public void testUpdate() {
        BatisUser user = this.usersMapper.getById(10L);
        System.out.println(user.toString());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd");
        LocalDate birthday = LocalDate.parse("2010-01-12",dateTimeFormatter);
        user.setBirthday(birthday);
        this.usersMapper.update(user);
    }

    @Test
    public void testQueryOne() {
        BatisUser user = this.usersMapper.getById(1L);
        System.err.println(user);
    }

    @Test
    public void testQueryByParam() throws ParseException {
        BatisUser user = new BatisUser();
        //user.setUserName("sunqi");
        //user.setBirthday(LocalDate.parse("2015-07-09"));
        user.setStartTime("2021-05-07 02:00:00");
        user.setEndTime("2021-05-09 02:01:08");
        List<BatisUser> list = this.usersMapper.queryByParam(user);
        list.stream().forEach(System.out::println);
        System.err.println(list);
    }


}