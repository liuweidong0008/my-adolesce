package com.adolesce.server.bloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.adolesce.server.bloomFilter.BloomFileter.MisjudgmentRate.VERY_SMALL;

/**
 * @version 1.0
 * @description: 布隆过滤器测试类
 */
public class BloomFilterTest {
    /**
     * 测试自定义的布隆过滤器
     */
    @Test
    public void test1() {
        BloomFileter fileter = new BloomFileter(7);
        System.out.println(fileter.addIfNotExist("abc"));
        System.out.println(fileter.addIfNotExist("abcd"));
        System.out.println(fileter.addIfNotExist("abcde"));
        System.out.println(fileter.addIfNotExist("11111"));
        System.out.println(fileter.addIfNotExist("22222"));
        System.out.println(fileter.addIfNotExist("33333"));

        System.out.println(fileter.addIfNotExist("abc"));
        System.out.println(fileter.addIfNotExist("33333"));

        //将过滤器当前状态保存至文件，以备后续使用
        fileter.saveFilterToFile("D:\\11.obj");
        //将文件中的数据恢复成布隆过滤器(里面有之前过滤的很多数据记录)
        fileter = BloomFileter.readFilterFromFile("D:\\11.obj");
        System.out.println(fileter.getUseRate());
        System.out.println(fileter.addIfNotExist("44444"));
        System.out.println(fileter.addIfNotExist("abcde"));
    }

    /**
     * 测试自定义的布隆过滤器误判率
     */
    @Test
    public void test2() {
        int dataCount = 1000000;//预计要插入多少数据
        BloomFileter.MisjudgmentRate fpp = VERY_SMALL;//期望的误判率

        BloomFileter fileter = new BloomFileter(fpp,dataCount,null);

        //插入数据
        for (int i = 0; i < 1000000; i++) {
            fileter.add(String.valueOf(i));
        }
        int count = 0;
        for (int i = 2000000; i < 3000000; i++) {
            boolean flag = fileter.check(String.valueOf(i));
            if (!flag) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println("总共的误判数:" + count);
    }


    /**
     * 测试谷歌布隆过滤器
     */
    @Test
    public void test3() throws IOException {
        int dataCount = 7;//预计要插入多少数据
        double fpp = 0.001;//期望的误判率
        BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), dataCount, fpp);

        System.out.println(filter.put("abc"));
        System.out.println(filter.put("abcd"));
        System.out.println(filter.put("abcde"));
        System.out.println(filter.put("11111"));
        System.out.println(filter.put("22222"));
        System.out.println(filter.put("33333"));

        System.out.println(filter.put("abc"));
        System.out.println(filter.put("33333"));

        //将过滤器当前状态保存至文件，以备后续使用
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\22.obj"));
        filter.writeTo(oos);

        //将文件中的数据恢复成布隆过滤器(里面有之前过滤的很多数据记录)
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D:\\22.obj"));
        BloomFilter<CharSequence> bloomFilter = BloomFilter.readFrom(ois, Funnels.stringFunnel(Charset.forName("UTF-8")));

        System.out.println(bloomFilter.put("44444"));
        System.out.println(bloomFilter.put("abcde"));
    }

    /**
     * 测试谷歌布隆过滤器误判率
     */
    @Test
    public void test4() {
        int dataCount = 1000000;//预计要插入多少数据
        double fpp = 0.5;//期望的误判率

        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), dataCount, fpp);
        //插入数据
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(String.valueOf(i));
        }
        int count = 0;
        for (int i = 1000000; i < 2000000; i++) {
            if (bloomFilter.mightContain(String.valueOf(i))) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println("总共的误判数:" + count);
    }
}
