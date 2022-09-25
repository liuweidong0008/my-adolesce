package com.adolesce.server.javabasic;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Java中四舍五入
 * 1、Math中四舍五入的方法
 *
 *      Math.ceil(double a)向上舍入，将数值向上舍入为最为接近的整数，返回值是double类型
 *
 *      Math.floor(double a)向下舍入，将数值向下舍入为最为接近的整数，返回值是double类型
 *
 *      Math.round(float a)标准舍入，将数值四舍五入为最为接近的整数，返回值是int类型
 *
 *      Math.round(double a)标准舍入，将数值四舍五入为最为接近的整数，返回值是long类型
 *
 * 2、Math中random生成随机数
 *
 *      Math.random()生成大于等于0，小于1的随机数
 *
 * 3、Random类生成随机数
 *
 *      两种构造方式：第一种使用默认的种子（当前时间作为种子），另一个使用long型整数为种子，Random类可以生成布尔型、浮点类型、整数等类型的随机数，还可以指定生成随机数的范围
 *
 * 4、BigDecimal处理小数
 *
 *     两种构造方式：第一种直接value写数字的值，第二种用String
 */

public class NumberTest {
    public static void main(String[] args) {
        //ceil返回大的值
        System.out.println(Math.ceil(-10.1));   //-10.0
        System.out.println(Math.ceil(10.7));    //11.0
        System.out.println(Math.ceil(-0.7));    //-0.0
        System.out.println(Math.ceil(0.0));     //0.0
        System.out.println(Math.ceil(-0.0));    //-0.0
        System.out.println(Math.ceil(-1.7));    //-1.0

        //floor返回小的值
        System.out.println(Math.floor(-10.1));   //-11.0
        System.out.println(Math.floor(10.7));    //10.0
        System.out.println(Math.floor(-0.7));    //-1.0
        System.out.println(Math.floor(0.0));     //0.0
        System.out.println(Math.floor(-0.0));    //-0.0
        System.out.println(Math.floor(-1.7));    //-2.0

        //round四舍五入，float返回int，double返回long
        System.out.println(Math.round(10.5));   //11
        System.out.println(Math.round(-10.5));  //-10

        //Math生成随机数
        System.out.println(Math.random());

        //Random类生成随机数
        Random rand = new Random();
        System.out.println(rand.nextBoolean());
        System.out.println(rand.nextDouble());
        System.out.println(rand.nextInt());
        System.out.println(rand.nextInt(10));

        //Random使用当前时间作为Random的种子
        Random rand2 = new Random(System.currentTimeMillis());
        System.out.println(rand2.nextBoolean());
        System.out.println(rand2.nextDouble());
        System.out.println(rand2.nextInt());
        System.out.println(rand2.nextInt(10));
        System.out.println(rand2.nextInt(5));

        //ThreadLocalRandom
        ThreadLocalRandom rand3 = ThreadLocalRandom.current();
        System.out.println(rand3.nextInt(5, 10));

        //BigDecimal
        System.out.println(0.8 - 0.7);   //0.10000000000000009
        BigDecimal a1 = new BigDecimal(0.1);
        BigDecimal b1 = new BigDecimal(0.9);
        BigDecimal c1 = a1.add(b1);
        System.out.println("a1.add(b1)=" + c1);  //a1.add(b1)=1.0000000000000000277555756156289135105907917022705078125

        BigDecimal a2 = new BigDecimal("0.1");
        a2 = BigDecimal.valueOf(0.1);

        BigDecimal b2 = new BigDecimal("0.9");
        b2 = BigDecimal.valueOf(0.9);

        BigDecimal c2 = a2.add(b2);
        System.out.println("a2=" + a2);  //a2=0.1
        System.out.println("a2.add(b2)=" + c2);  //a2.add(b2)=1.0
    }

    /**
     * apache工具类随机数获取
     */
    @Test
    public void testRandomUtil() {
        System.out.println(RandomStringUtils.randomNumeric(6));
    }


    @Test
    public void test1(){
        Object a = new Integer(3);
        Long b = Long.valueOf(a.toString());
        System.out.println(b);
    }

    @Test
    public void testBigDecimal() {
        BigDecimal a = new BigDecimal(10.897);
        BigDecimal b = new BigDecimal("10.897");
        BigDecimal c = BigDecimal.valueOf(10.897);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

    @Test
    public void test2() {
       Integer i01 = 59;
       int i02 = 59;
       Integer i03 = Integer.valueOf(59);
       Integer i04 = new Integer(59);

        System.out.println(i02 == i04);
        System.out.println(i03 == i04);
        System.out.println(i01 == i03);
        System.out.println(i01 == i02);
    }
}