package com.adolesce.server.javabasic;

import cn.hutool.core.collection.CollUtil;
import com.adolesce.common.entity.User;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: 集合测试类
 * @date 2021/10/26 10:40
 */
public class CollectionTest {

    //集合转Map
    @Test
    public void testListToMap(){
        List<User> users = this.getUsers(5);
        //通过Hutool工具转
        Map<Long, User> userMap = CollUtil.fieldValueMap(users, "id");
        //通过Stream流转
        userMap = users.stream().collect(Collectors.toMap(u -> u.getId(), Function.identity()));
        userMap.forEach((k,v) -> System.out.println(k+" : "+v));


        Map<Long, String> valueAsMap = CollUtil.fieldValueAsMap(users, "id", "userName");
        valueAsMap = users.stream().collect(Collectors.toMap(u -> u.getId(),u -> u.getUserName()));
        valueAsMap.forEach((k,v) -> System.out.println(k+" : "+v));
    }

    //从集合中获取某属性的集合
    @Test
    public void testGetValuesForCollection(){
        List<User> users = this.getUsers(5);
        //通过Hutool工具获取
        List<String> userNames = CollUtil.getFieldValues(users, "userName",String.class);
        //通过Stream流获取
        //userNames = users.stream().map(u -> u.getUserName()).collect(Collectors.toList());
        userNames.forEach(System.out::println);

        System.out.println(LocalDate.now());
        System.out.println(LocalDateTime.now());
        System.out.println(new Date());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private List<User> getUsers(int count) {
        List<User> users = new ArrayList<>(count);
        User user;
        for (int i = 1; i <= count; i++) {
            user = new User();
            user.setId(Long.valueOf(i));
            user.setUserName("lwd"+i);
            user.setAge(10+i);
            user.setSex(1);
            users.add(user);
        }

        Map<String,String> map = new HashMap<>();
        map.keySet();
        map.values();
        map.entrySet();

        map.forEach((k,v) -> System.out.println(k +":" +v));
        return users;
    }


}
