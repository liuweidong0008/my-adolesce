package com.adolesce.server.cache.redis;

import com.adolesce.server.utils.redis.RedisUtil;
import lombok.Data;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/13 18:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设置缓存过期时间
     *
     * @throws Exception void
     * @Title testExpire
     */
    @Test
    public void testExpire() throws Exception {
        redisUtil.set("aaaKey", "aaaValue");
        redisUtil.expire("aaaKey", 100);
        Assert.assertEquals(redisUtil.get("aaaKey"), "aaaValue");
        TimeUnit.SECONDS.sleep(100);
        Assert.assertNotEquals(redisUtil.get("aaaKey"), "aaaValue");

    }

    @Test
    public void testGetExpire() throws Exception {
        redisUtil.set("aaaKey", "aaaValue");
        redisUtil.expire("aaaKey", 100);
        // 设置了缓存就会及时的生效，所以缓存时间小于最初设置的时间
        Assert.assertTrue(redisUtil.getExpire("aaaKey") < 100L);
    }

    @Test
    public void testHasKey() throws Exception {
        redisUtil.set("aaaKey", "aaaValue");
        // 存在的
        Assert.assertTrue(redisUtil.hasKey("aaaKey"));
        // 不存在的
        Assert.assertFalse(redisUtil.hasKey("bbbKey"));
    }

    @Test
    public void testDel() throws Exception {
        redisUtil.set("aaaKey", "aaaValue");
        // 存在的
        Assert.assertTrue(redisUtil.hasKey("aaaKey"));
        redisUtil.del("aaaKey");
        Assert.assertFalse(redisUtil.hasKey("bbbKey"));
    }

    @Test
    public void testGet() throws Exception {
        redisUtil.set("aaaKey", "aaaValue");
        Assert.assertEquals(redisUtil.get("aaaKey"), "aaaValue");
    }

    @Test
    public void testSetStringObject() throws Exception {
        Assert.assertTrue(redisUtil.set("aaaKey", "aaaValue"));
    }

    @Test
    public void testSetStringObjectLong() throws Exception {
        Assert.assertTrue(redisUtil.set("aaaKeyLong", 100L));
    }

    @Test
    public void testSetObject() {
        // 测试对象
        TestModel testModel = new TestModel();
        testModel.setId(System.currentTimeMillis());
        testModel.setName("测试");
        redisUtil.set("testModel", testModel);
        TestModel testModel2 = (TestModel) redisUtil.get("testModel");
        System.err.println(testModel2);
        System.err.println(testModel2.getName());
        System.err.println(testModel2.getId());
    }

    @Test
    @Ignore
    public void testIncr() throws Exception {
        String key = "testIncr";
        redisUtil.incr(key, 1);
        redisUtil.expire(key, 10); // 缓存失效10s
        Assert.assertEquals(redisUtil.get(key), 1);
    }

    // 高并发下 递增 测试
    @Test
    @Ignore
    public void testIncr2() throws Exception {
        // 模拟发送短信的并发
        // 首先开启一个线程池，创建一个专门消费短信的线程
        // 一次性放入多个线程实例 ，实例 都是2秒请求一次 ，而10s内的只能允许一条。 也就是说我测试100个线程，只能10s一条
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 6, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //
                    String key = "testIncr2_17353620612";
                    long count = redisUtil.incr(key, 1);
                    if (count == 1L) {
                        redisUtil.expire(key, 10); // 缓存失效10s
                        System.err.println("短信发送成功===" + new Date());

                    } else {
                        System.err.println("访问次数快===" + new Date());
                    }
                }
            });
            threadPoolExecutor.submit(threads[i]);
        }

        while (threadPoolExecutor.getQueue().isEmpty()) {
            threadPoolExecutor.shutdown();
            System.err.println("所有线程执行完毕");
        }

        System.in.read();// 加入该代码，让主线程不挂掉

//        // 启动线程
//        for (int i = 0; i < 100; i++) {
//            threads[i].start();
//        }
    }

    long count = 0L;

    // 高并发下 错误的测试 递增 测试
    @Test
    @Ignore
    public void testIncr3() throws Exception {
        // 模拟发送短信的并发

        // 首先开启一个线程池，创建一个专门消费短信的线程
        // 一次性放入多个线程实例 ，实例 都是2秒请求一次 ，而10s内的只能允许一条。 也就是说我测试100个线程，只能10s一条
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 6, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        TimeUnit.SECONDS.sleep(2);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    pool-3-thread-1count===1
//                   pool-3-thread-5count===1
                    String key = "testIncr2_17353620612";
                    count = count + 1;
                    System.err.println(Thread.currentThread().getName() + "count===" + count);
                    // 督导的count
                    // if (count == 1L) {
//                        count = count - 1;
//                        System.err.println("短信发送成功===" + new Date());
//                    } else {
//                        System.err.println("访问次数快===" + new Date());
//                    }
                }
            });
            threadPoolExecutor.submit(threads[i]);
        }

        while (threadPoolExecutor.getQueue().isEmpty()) {
            threadPoolExecutor.shutdown();
            System.err.println("所有线程执行完毕");
        }

//        System.in.read();// 加入该代码，让主线程不挂掉
    }

    @Test
    @Ignore
    public void testDecr() throws Exception {
        String key = "Decr_17353620612";
        redisUtil.decr(key, 1);
        redisUtil.expire(key, 10); // 缓存失效10s
        Assert.assertEquals(redisUtil.get(key), 1);
    }

    @Test
    public void testHget() throws Exception {
        redisUtil.hset("testHget", "testHget", "testHget");
        Assert.assertEquals("testHget", redisUtil.hget("testHget", "testHget"));

    }

    @Test
    public void testHmget() throws Exception {
        redisUtil.hset("testHmget", "testHmget1", "testHmget1");
        redisUtil.hset("testHmget", "testHmget2", "testHmget2");
        Map<Object, Object> map = redisUtil.hgetAll("testHmget");
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<Object, Object> e : map.entrySet()) {
                System.err.println(e.getKey() + "===" + e.getValue());
            }
        }
    }

    @Test
    public void testHsetStringStringObject() throws Exception {
        redisUtil.hset("map", "testHsetStringStringObject", "testHsetStringStringObject");

    }

    // 测试放在hash 里面的对象
    @Test
    public void testHsetObject() {
        // 测试对象
        TestModel testModel = new TestModel();
        testModel.setId(System.currentTimeMillis());
        testModel.setName("测试");
        redisUtil.hset("hash", "testModel", testModel);
        TestModel testModel2 = (TestModel) redisUtil.hget("hash", "testModel");
        System.err.println(testModel2);
        System.err.println(testModel2.getName());
        System.err.println(testModel2.getId());
    }

    // 太奇妙了 放进去Long 取出来会根据大小变为相应的数据类型
    @Test
    public void testHsetStringStringObjectLong() throws Exception {
        redisUtil.hset("testHsetStringStringObjectLong", "int", 100); // java.lang.Integer 读取来是inter
        System.err.println(redisUtil.hget("testHsetStringStringObjectLong", "int").getClass().getTypeName());
//        Assert.assertEquals(redisUtil.hget("map", "testHsetStringStringObject"), 100L);
        redisUtil.hset("testHsetStringStringObjectLong", "long", System.currentTimeMillis()); // java.lang.Integer 读取来是inter
        System.err.println(redisUtil.hget("testHsetStringStringObjectLong", "long").getClass().getTypeName());

    }

    @Test
    public void testHdel() throws Exception {
        redisUtil.hset("testHdel", "int", 100);
        Assert.assertEquals(redisUtil.hget("testHdel", "int"), 100);
        redisUtil.hdel("testHdel", "int");
        Assert.assertEquals(redisUtil.hget("testHdel", "int"), null);

    }

    @Test
    public void testHHasKey() throws Exception {
        redisUtil.hset("testHHasKey", "int", 100);
        Assert.assertTrue(redisUtil.hHasKey("testHHasKey", "int"));

    }

    @Test
    public void testHincr() throws Exception {
        System.err.println(redisUtil.hincr("testHincr", "testHincr", 1));

    }

    @Test
    public void testHdecr() throws Exception {
        System.err.println(redisUtil.hincr("testHincr", "testHincr", 1));
    }

    @Test
    public void testSGet() throws Exception {
        redisUtil.sSet("testSGet", "testSGet1");
        redisUtil.sSet("testSGet", "testSGet2");
        System.err.println(StringUtils.join(redisUtil.sGet("testSGet"), ","));
    }

    @Test
    public void testSHasKey() throws Exception {
        redisUtil.sSet("testSHasKey", "testSHasKey");
        Assert.assertTrue(redisUtil.sHasKey("testSHasKey", "testSHasKey"));

    }

    @Test
    public void testSSet() throws Exception {
        redisUtil.sSet("testSSet", "testSSet");

    }

    @Test
    public void testSSetAndTime() throws Exception {
        redisUtil.sSetAndTime("testSSetAndTime", 20, "testSSetAndTime1");
        redisUtil.sSetAndTime("testSSetAndTime", 5, "testSSetAndTime2");
        System.err.println(StringUtils.join(redisUtil.sGet("testSSetAndTime"), ","));
        TimeUnit.SECONDS.sleep(5);
        System.err.println(StringUtils.join(redisUtil.sGet("testSSetAndTime"), ","));
        TimeUnit.SECONDS.sleep(20);
        System.err.println(StringUtils.join(redisUtil.sGet("testSSetAndTime"), ","));

    }

    @Test
    public void testSGetSetSize() throws Exception {
        redisUtil.sSetAndTime("testSGetSetSize", 20, "testSGetSetSize1");
        redisUtil.sSetAndTime("testSGetSetSize", 5, "testSGetSetSize");
        Assert.assertEquals(redisUtil.sGetSetSize("testSGetSetSize"), 2);
    }

    @Test
    public void testSetRemove() throws Exception {
        redisUtil.sSetAndTime("testSetRemove", 20, "testSetRemove1");
        redisUtil.sSetAndTime("testSetRemove", 5, "testSetRemove");
        Assert.assertEquals(redisUtil.sGetSetSize("testSetRemove"), 2);
        redisUtil.setRemove("testSetRemove", "testSetRemove");
        Assert.assertEquals(redisUtil.sGetSetSize("testSetRemove"), 1);

    }

    @Test
    public void testLGet() throws Exception {
        redisUtil.lSet("testLGet", "testLGet0", 10); // 10秒过期
        redisUtil.lSet("testLGet", "testLGet1", 10);
        // 查询三个元素 2-0+1
        List<Object> list = redisUtil.lGet("testLGet", 0, 2);
        System.err.println("list===" + list);
        // 查询两个
        List<Object> list2 = redisUtil.lGet("testLGet", 0, 1);
        System.err.println("list2===" + list2);
        // 查询全部
        List<Object> list3 = redisUtil.lGet("testLGet", 0, -1);
        System.err.println("list3===" + list3);

    }

    @Test
    public void testLGetListSize() throws Exception {
        // 看看重复元素会怎么处理
        long size = 0;
        redisUtil.lSet("testLGetListSize", "testLGetListSize0", 10); // 10秒过期
        size = redisUtil.lGetListSize("testLGetListSize");
        System.err.println(size);
        redisUtil.lSet("testLGetListSize", "testLGetListSize0", 10);
        size = redisUtil.lGetListSize("testLGetListSize");
        System.err.println(size);
    }

    //
    @Test
    public void testLGetIndex() throws Exception {
        redisUtil.lSet("testLGetIndex", "testLGetIndex0", 10); // 10秒过期
        redisUtil.lSet("testLGetIndex", "testLGetIndex1", 10);
        Object obj = redisUtil.lGetIndex("testLGetIndex", 0);
        Assert.assertEquals(obj, "testLGetIndex0");
    }

    @Test
    public void testLSetStringObject() throws Exception {
        redisUtil.lSet("testLSetStringObject", "testLSetStringObject0");
        redisUtil.lSet("testLSetStringObject", "testLSetStringObject1");
        redisUtil.lSet("testLSetStringObject", "testLSetStringObject2");
    }

    @Test
    public void testLSetStringObjectLong() throws Exception {

    }

    @Test
    public void testLUpdateIndex() throws Exception {
        redisUtil.lSet("testLUpdateIndex", "testLUpdateIndex0");
        redisUtil.lSet("testLUpdateIndex", "testLUpdateIndex1");
        redisUtil.lSet("testLUpdateIndex", "testLUpdateIndex2");
        Object obj = redisUtil.lUpdateIndex("testLUpdateIndex", 0, "更新的");
        Assert.assertEquals("更新的", obj);
    }

    @Test
    public void testLRemove() throws Exception {
        redisUtil.lSet("testLRemove", "testLRemove0");
        redisUtil.lSet("testLRemove", "testLRemove1");
        redisUtil.lSet("testLRemove", "testLRemove2");
        Object obj = redisUtil.lRemove("testLRemove", 1, "testLRemove2");
    }
}

@Data
class TestModel implements Serializable {
    /**
     * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
}
