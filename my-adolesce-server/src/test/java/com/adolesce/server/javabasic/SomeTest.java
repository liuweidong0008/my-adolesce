package com.adolesce.server.javabasic;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.adolesce.common.entity.User;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/7/13 15:32
 */
public class SomeTest {
    @Test
    public void testInt1() {
        Integer i = new Integer(100);
        Integer j = new Integer(100);
        System.out.println(i == j);
    }

    @Test
    public void testInt2() {
        Integer i = new Integer(100);
        int j = 100;
        System.out.println(i == j);
    }

    @Test
    public void testInt3() {
        Integer i = new Integer(100);
        Integer j = 100;
        System.out.println(i == j);
    }

    @Test
    public void testInt4() {
        Integer i = 100;
        Integer j = 100;
        System.out.println(i == j);

        Integer x = 128;
        Integer y = 128;
        System.out.println(x == y);
    }

    @Test
    public void testString1() {
        String s = "hello";
        String ss = s;
        System.out.println(s == ss);
        s = s + " world";
        System.out.println(s == ss);
        System.out.println(s);
    }

    @Test
    public void testString2() {
        String s1 = "hello";
        String s2 = new String("hello");
        System.out.println(s1 == s2);
    }

    @Test
    public void testString3() {
        String s1 = new String("hello ") + new String("world");
        s1.intern();
        String s2 = "hello world";
        System.out.println(s1 == s2);
    }

    @Test
    public void testString4() {
        String s1 = new String("hello ") + new String("world");
        String s2 = "hello world";
        System.out.println("s1 == s2: " + (s1 == s2));

        String s3 = "hello " + "world";

        String h = "hello ";
        String w = "world";
        String s4 = h + w;

        String s5 = "hello";
        s5 = s5 + " world";

        System.out.println("s1 == s3: " + (s1 == s3));
        System.out.println("s1 == s4: " + (s1 == s4));

        System.out.println("s2 == s3: " + (s2 == s3));
        System.out.println("s2 == s4: " + (s2 == s4.intern()));
        System.out.println("s3 == s4: " + (s3 == s4.intern()));

        System.out.println("s1 == s5: " + (s1 == s5));
        System.out.println("s2 == s5: " + (s2 == s5));
        System.out.println("s3 == s5: " + (s3 == s5));
        System.out.println("s4 == s5: " + (s4 == s5));
    }

    @Test
    public void testTryFinally() {
        System.out.println("return :" + testReturn5());
    }

    private int testReturn1() {
        int i = 1;
        try {
            i++;
            System.out.println("try:" + i);
            return i;
        } catch (Exception e) {
            i++;
            System.out.println("catch:" + i);
        } finally {
            i++;
            System.out.println("finally:" + i);
        }
        return i;
    }

    private List<Integer> testReturn2() {
        List<Integer> list = new ArrayList<>();
        try {
            list.add(1);
            System.out.println("try:" + list);
            return list;
        } catch (Exception e) {
            list.add(2);
            System.out.println("catch:" + list);
        } finally {
            list.add(3);
            System.out.println("finally:" + list);
        }
        return list;
    }

    private int testReturn3() {
        int i = 1;
        try {
            i++;
            System.out.println("try:" + i);
            int x = i / 0;
        } catch (Exception e) {
            i++;
            System.out.println("catch:" + i);
            return i;
        } finally {
            i++;
            System.out.println("finally:" + i);
        }
        return i;
    }

    private int testReturn4() {
        int i = 1;
        try {
            i++;
            System.out.println("try:" + i);
            return i;
        } catch (Exception e) {
            i++;
            System.out.println("catch:" + i);
            return i;
        } finally {
            i++;
            System.out.println("finally:" + i);
            return i;
        }
    }

    private int testReturn5() {
        int i = 1;
        try {
            i++;
            System.out.println("try:" + i);
            int x = i / 0;
            return i;
        } catch (Exception e) {
            i++;
            System.out.println("catch:" + i);
            return i;
        } finally {
            i++;
            System.out.println("finally:" + i);
            return i;
        }
    }

    @Test
    public void testStringNull() {
        String s1 = null + "";
        String s2 = "null";
        System.out.println(s1 + "");
        System.out.println(s2);
        System.out.println(s1.equals(s2));
        System.out.println((Boolean) null);
    }

    @Test
    public void testDateBetweenSecond() throws ParseException {
        String str = DateUtil.tomorrow().toDateStr() + " 00:00:00";
        long between = DateUtil.between(new Date(), DateUtil.parse(str), DateUnit.SECOND);
        System.out.println(between);
    }

    @Test
    public void testPageBySelf() {
        Integer page = 7;
        Integer pageSize = 3;
        String pids = "25,13,67,90,26,63,73,88,54,18,90,16,17,52,99";
        //List<String> list = Arrays.asList(pids.split(","));
        String[] list = pids.split(",");

       /* int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize - 1;
        System.out.println(startIndex + " -> "+endIndex);

        List<Long> result = new ArrayList<>(pageSize);
        for(int i = startIndex; i <= endIndex; i++ ){
            result.add(Long.valueOf(list[i]));
        }
        result.forEach(System.out::println);*/


        Arrays.stream(list).skip((page - 1) * pageSize).limit(pageSize)
                .map(e -> Long.valueOf(e))
                .forEach(System.out::println);
        // int totalPageSize = list.size() % pageSize ==0 ? list.size() / pageSize : list.size() / pageSize + 1;
    }


    @Test
    public void testRandom() {
        for (int i = 0; i < 20; i++) {
            //Integer num = RandomUtils.nextInt(2,13);
            //Integer num = RandomUtils.nextInt(11) + 2 ;
            Integer num = RandomUtil.randomInt(2, 13);
            System.out.println(num);
        }
    }

    @Test
    public void testToString() {
        User user = new User();
        System.out.println(user.toString());
        List list = new ArrayList();
        list = new LinkedList();
        Map map = new HashMap();
    }

    @Test
    public void testDate() {
        Date endOfDay = DateUtil.endOfDay(new Date());
        //一天后过期   获取当前凌晨时间  减去当前时间  得到毫秒值
        long betweenDay = DateUtil.between(new Date(System.currentTimeMillis()), endOfDay, DateUnit.SECOND);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endOfDay));
    }

    @Test
    public void test1() {
        String str = "[\"aaa\",\"bbb\",\"ccc\"]";
        //方式一
        List<String> strings = JSON.parseObject(str, List.class);
        strings.forEach(System.out::println);
        System.out.println("==============");
        //方式二
        strings = (List) JSON.parseArray(str);
        strings.forEach(System.err::println);
    }

    @Test
    public void test2() throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd");

        System.out.println(format1.format(new Date()));
        System.out.println(format2.format(new Date()));

        String dateStr1 = "2020-12-26";
        String dateStr2 = "2020-12-27";
        String formatStr1 = format1.format(format1.parse(dateStr1));
        String formatStr2 = format1.format(format1.parse(dateStr2));

        String formatStr3 = format2.format(format2.parse(dateStr1));
        String formatStr4 = format2.format(format2.parse(dateStr2));

        System.out.println(formatStr1);
        System.out.println(formatStr2);
        System.out.println(formatStr3);
        System.out.println(formatStr4);
    }


}
