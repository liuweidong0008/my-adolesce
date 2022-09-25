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

        map.forEach((key,value) -> System.out.println(key +":" +value));

        for(String key:map.keySet()){
            System.out.println(key + ":" + map.get(key));
        }

        for(Map.Entry<String, String> entry:map.entrySet()){
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        return users;
    }

    private void getUsers(List<User> userList, int i,String name) {
        User users = new User();
        users.setId(Long.valueOf(i));
        users.setUserName(name + i);
        users.setAge(i);
        users.setSex(i % 2 + 1);
        userList.add(users);
    }

    private List<User> getDBUser() {
        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            getUsers(userList, i,"zhangsan");
        }
        return userList;
    }

    private List<User> getDisanfangUser() {
        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            getUsers(userList, i,"lisi");
        }
        return userList;
    }

    @Test
    public void testDeleteBeanFromList() {
        List<User> dbUser = this.getDBUser();
        List<User> disanfangUser = this.getDisanfangUser();
        Set<Long> dbUserIds = dbUser.stream().map(u -> u.getId()).collect(Collectors.toSet());

        //方式一：用另一个集合接受第三方集合过滤后的结果集
        /*List<User> result = disanfangUser.stream().filter(u -> !dbUserIds.contains(u.getId())).collect(Collectors.toList());
        result.forEach(System.out::println);*/

        //方式二：在第三方集合本身中去删除重复数据，有如下两种方式（注意：不能用增强for去删除本集合的数据）
        //2.1、普通for循环删除本集合数据
        /*for (int i = 0; i < disanfangUser.size(); i++) {
            if(dbUserIds.contains(disanfangUser.get(i).getId())){
                disanfangUser.remove(disanfangUser.get(i));
                i--;  //由于删除了当前元素，集合大小再发生变化，迭代的角标要向前退一
            }
        }*/

        //2.2、利用迭代器去删除本集合数据
        /*Iterator<User> iterator = disanfangUser.iterator();
        while (iterator.hasNext()){
            if(dbUserIds.contains(iterator.next().getId())){
                iterator.remove();
            }
        }*/

        //2.3、利用增强for删除（报异常：ConcurrentModificationException）
        for(User user:disanfangUser){
            if(dbUserIds.contains(user.getId())){
                disanfangUser.remove(user);
            }
        }

        //打印第三方集合结果
        disanfangUser.forEach(System.out::println);
    }


}
