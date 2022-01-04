package com.adolesce.server.javabasic.hmtest.reflex;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/8 11:54
 */
public class ReflexTest {

    @Test
    public void testMapDeclared() {
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("like_name","zhangsan");
        paramMap.put("ge_price",10);
        paramMap.put("le_price",20);
        paramMap.put("in_code", Arrays.asList(new Integer[]{2,5,6,8}));

        //获取实体类所有属性名
        Field[] goodsEntityFields = GoodsEntity.class.getDeclaredFields();
        Set<String> goodsEntityFieldNames = Arrays.stream(goodsEntityFields)
                      .map(field -> field.getName()).collect(Collectors.toSet());

        //获取Compare类的方法对象Map（方法名称 -> 方法对象） 弊端：同名方法存在覆盖
        /*Method[] mybatisPlusMethods = Compare.class.getDeclaredMethods();
        Map<String, Method> mybatisPlusMethodMap = Arrays.stream(mybatisPlusMethods).collect(Collectors.toMap(
                method -> method.getName(), Function.identity(), (o1, o2) ->
                         o1.getParameterCount() < o2.getParameterCount()? o1:o2
        ));*/

        //循环前端传参列表，反射设置查询条件
        QueryWrapper queryWrapper = new QueryWrapper();
        paramMap.forEach((k,v) -> {
            String[] karr = k.split("_");
            if(karr.length > 1){
                String operator = karr[0];
                String fieldName = karr[1];
                if(goodsEntityFieldNames.contains(fieldName)){
                    try {
                        //Method method = mybatisPlusMethodMap.get(operator);
                        Method method;
                        if(v instanceof List){
                            method = Func.class.getDeclaredMethod(operator,Object.class,Collection.class);
                        }else{
                            method = Compare.class.getDeclaredMethod(operator,Object.class,Object.class);
                        }
                        if(ObjectUtil.isNotEmpty(method)){
                            method.invoke(queryWrapper,fieldName,v);
                            System.out.println(operator+":"+fieldName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println(queryWrapper);
    }
}
