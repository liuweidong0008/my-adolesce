package com.adolesce.server.jdk8Speciality;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.annotation.*;

public class OtherSpeciality {
    @Test
    public void testNashornJS() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

        String name = "Runoob";
        Integer result = null;

        try {
            nashorn.eval("print('" + name + "')");
            result = (Integer) nashorn.eval("10 + 2");

        } catch (ScriptException e) {
            System.out.println("执行脚本错误: " + e.getMessage());
        }

        System.out.println(result.toString());
    }


    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Filters {
        Filter[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Filters.class)
    public @interface Filter {
        String value1();

        String value2();
    }

    ;

    @Filter(value1 = "filter1", value2 = "111")
    @Filter(value1 = "filter2", value2 = "222")
    // @Filters({@Filter( value="filter1",value2="111" ),@Filter(value="filter2", value2="222")}).注意：JDK8之前：1.没有@Repeatable  2.采用本行“注解容器”写法
    public interface Filterable {
    }

    @Test
    public void testRepeatable() {
        // 获取注解后遍历打印值
        for (Filter filter : Filterable.class.getAnnotationsByType(Filter.class)) {
            System.out.println(filter.value1() + filter.value2());
        }
    }
}
