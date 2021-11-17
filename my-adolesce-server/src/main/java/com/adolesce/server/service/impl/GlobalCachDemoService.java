package com.adolesce.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class GlobalCachDemoService {
    private static Integer SLEEP_TIME = 3_000;
    //模拟数据库查询
    public Map<String, Object> action1() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "action1");
        result.put("no", 1);
        result.put("address", "衡阳");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //模拟数据库查询
    public Map<String, Object> action2() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "action2");
        result.put("no", 2);
        result.put("address", "岳阳");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //模拟数据库查询
    public Map<String, Object> action3() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "action3");
        result.put("no", 3);
        result.put("address", "浏阳");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //模拟数据库查询
    public Map<String, Object> action4() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "action4");
        result.put("no", 4);
        result.put("address", "益阳");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
