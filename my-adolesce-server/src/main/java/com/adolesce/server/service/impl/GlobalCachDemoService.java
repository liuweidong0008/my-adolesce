package com.adolesce.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class GlobalCachDemoService {
    //模拟数据库查询
    public Map<String, Object> action1() {
        Map<String,Object> result = new HashMap<>();
        result.put("name","action1");
        result.put("no",1);
        result.put("address","衡阳");
        return result;
    }
    //模拟数据库查询
    public Map<String, Object> action2() {
        Map<String,Object> result = new HashMap<>();
        result.put("name","action2");
        result.put("no",2);
        result.put("address","岳阳");
        return result;
    }
    //模拟数据库查询
    public Map<String, Object> action3() {
        Map<String,Object> result = new HashMap<>();
        result.put("name","action3");
        result.put("no",3);
        result.put("address","浏阳");
        return result;
    }
    //模拟数据库查询
    public Map<String, Object> action4() {
        Map<String,Object> result = new HashMap<>();
        result.put("name","action4");
        result.put("no",4);
        result.put("address","益阳");
        return result;
    }
}
