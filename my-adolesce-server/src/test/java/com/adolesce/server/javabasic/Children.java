package com.adolesce.server.javabasic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/11/19 15:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Children {
    private Children children;
    private String id;
    private String name;
    private String props;
    private Double[] sphere;
    private String type;

    public static void main(String[] args) {
        Children children4 = new Children();
        children4.setChildren(null);
        children4.setId("fhueiae892yrfhq9snd329080q57r0f0qje492q0ldf03jhq0");
        children4.setName("一个文件");
        children4.setProps("");
        children4.setSphere(new Double[]{ -2177720.62167222,  4388715.55081648,4070071.87837201, 21.7164586711409});
        children4.setType("element");

        Children children3 = new Children();
        children3.setChildren(children4);
        children3.setId("d2q3m09rt0f7uje0q2480qurjd0q94uq0hq0jeqwpe2iq90");
        children3.setName("文件aaaaa");
        children3.setProps("");
        children3.setSphere(null);
        children3.setType("category");

        Children children2 = new Children();
        children2.setChildren(children3);
        children2.setId("adc");
        children2.setName("adc");
        children2.setProps("");
        children2.setSphere(null);
        children2.setType("file");

        Children children1 = new Children();
        children1.setChildren(children2);
        children1.setId("test");
        children1.setName("test");
        children1.setProps("");
        children1.setSphere(null);
        children1.setType("test");

        Map<String,Children> map = new HashMap<>();
        getChildrenMap(children1,map);
        System.out.println(map);
    }

    private static void getChildrenMap(Children children,Map<String,Children> map) {
        map.put(children.getId(),children);
        if(children.getChildren() != null){
            getChildrenMap(children.getChildren(),map);
        }
    }
}
