package com.adolesce.common.entity.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/5 19:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    /**
     * 名字
     */
    private String name;
    /**
     * 工号
     */
    private String workNo;

    private static final Map<String, Teacher> teacherMap = new HashMap<>();

    static {
        teacherMap.put("峰哥", new Teacher("岳嫣峰", "90941"));
        teacherMap.put("博哥", new Teacher("袁文博", "88009"));
        teacherMap.put("堂哥", new Teacher("李书堂", "82143"));
        teacherMap.put("焱哥", new Teacher("胡焱", "88013"));
        teacherMap.put("明哥", new Teacher("唐志明", "88071"));
        teacherMap.put("培哥", new Teacher("郭培", "88032"));
        teacherMap.put("诚哥", new Teacher("唐诚", "88147"));
        teacherMap.put("通哥", new Teacher("刘通", "88034"));
        teacherMap.put("东哥", new Teacher("刘威东", "88133"));
    }

    public static Teacher getTeacher(String teacherNick) {
        return teacherMap.get(teacherNick);
    }
}
