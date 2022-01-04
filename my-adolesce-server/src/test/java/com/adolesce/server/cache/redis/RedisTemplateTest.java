package com.adolesce.server.cache.redis;

import com.adolesce.common.entity.Address;
import com.adolesce.common.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/14 22:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {
    //@Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RedisTemplate redisTemplate2;

    @Test
    public void testSaveStr() {
        redisTemplate2.opsForValue().set("testStr3","liuweidong");
        redisTemplate2.opsForValue().set("testStr4","刘威东");
        Object testStr1 = redisTemplate2.opsForValue().get("testStr3");
        Object testStr2 = redisTemplate2.opsForValue().get("testStr4");
        System.out.println(testStr1);
        System.out.println(testStr2);
    }

    /**
     * 测试字符串数据类型
     */
    @Test
    public void testString(){
        for (int i = 1; i < 10; i++) {
            redisTemplate.delete("strKey"+i);
        }

        //0、获取字符串类型操作对象
        ValueOperations<String, Object> strOperation = redisTemplate.opsForValue();

        //1、设置值
        strOperation.set("strKey1","str1");

        //2、设置值并设置有效期为1分钟
        strOperation.set("strKey2","str2",1, TimeUnit.MINUTES);
        strOperation.set("strKey3","str3", Duration.ofMinutes(1));

        //3、设置值并设置偏移量
        strOperation.set("strKey4","str4",10);

        //4、上锁（当lock1不存时才能进行设置）
        Boolean result1 = strOperation.setIfAbsent("lock1", "lockValue1");
        System.err.println("strOperation.setIfAbsent(\"lock1\", \"lockValue1\")  result = "+result1);

        //6、上锁并设置有效期
        Boolean result2 = strOperation.setIfAbsent("lock2","lockValue2",1,TimeUnit.MINUTES);
        System.err.println("strOperation.setIfAbsent(\"lock2\",\"lockValue2\",1,TimeUnit.MINUTES) result = "+result2);
        Boolean result3 = strOperation.setIfAbsent("lock3","lockValue3",Duration.ofMinutes(1));
        System.err.println("strOperation.setIfAbsent(\"lock3\",\"lockValue3\",1,TimeUnit.MINUTES) result = "+result3);

        //7、当key存在时才进行设置
        Boolean result4 =  strOperation.setIfPresent("strKey5","str5");
        System.err.println("strOperation.setIfPresent(\"strKey5\",\"str5\") result = "+result4);

        //8、先获取结果再进行设置
        Object result5 = strOperation.getAndSet("strKey6","str6");
        System.err.println("strOperation.getAndSet(\"strKey6\",\"str6\") result = "+result5);

        //9、批量设置
        Map<String,String> strMap = new HashMap<>();
        strMap.put("strKey7","str7");
        strMap.put("strKey8","str8");
        strOperation.multiSet(strMap);

        //9、当key不存在时再进行批量设置，有一个存在整个都不进行设置
        strMap = new HashMap<>();
        strMap.put("strKey1","str1");
        strMap.put("strKey9","str9");
        Boolean result6 = strOperation.multiSetIfAbsent(strMap);
        System.err.println("strOperation.multiSetIfAbsent(strMap) result = "+result6);

        //10、根据key获取value
        String result7 = (String) strOperation.get("strKey1");
        System.err.println("strOperation.get(\"strKey1\") result = "+result7);

        //11、根据key和开始、结束角标获取字符串（包头不包尾）
        String result8 = strOperation.get("lock1", 0, 2);
        System.err.println("strOperation.get(\"lock1\", 0, 2) result = "+result8);

        //12、根据key集合批量获取value集合
        List<Object> result9 = strOperation.multiGet(Arrays.asList("strKey1", "strKey6", "strKey7", "strKey8"));
        System.err.println("strOperation.multiGet(Arrays.asList(\"strKey1\", \"strKey6\", \"strKey7\", \"strKey8\")) result = "+result9);

        //13、递增1
        Long result10 = strOperation.increment("numKey1");
        System.err.println("strOperation.increment(\"numKey1\") result = "+result10);

        //14、递增3
        Long result11 = strOperation.increment("numKey2", 3);
        System.err.println("strOperation.increment(\"numKey2\", 3) result = "+result11);

        //15、递增1.56
        Double result12 = strOperation.increment("numKey3", 1.56);
        System.err.println("strOperation.increment(\"numKey3\", 1.56) result = "+result12);

        //16、递减1
        Long result13 = strOperation.decrement("numKey4");
        System.err.println("strOperation.decrement(\"numKey4\") result = "+result13);

        //17、递减3
        Long result14 = strOperation.decrement("numKey5", 3);
        System.err.println("strOperation.decrement(\"numKey5\", 3) result = "+result14);

        //18、设置对象
        User user  = new User();
        user.setUserName("张三");
        user.setAge(25);
        user.setSeriNo("Kxd2837298398");
        user.setAddress(new Address("辽宁省","吉林市"));
        strOperation.set("tUser",user);
        User tUser = (User) strOperation.get("tUser");
        System.out.println(tUser);
    }

    /**
     * 测试hash数据类型
     */
    @Test
    public void testHash(){
        redisTemplate.delete("hKey1");
        redisTemplate.delete("hKey2");
        redisTemplate.delete("hKey3");

        //0、获取hash类型操作对象
        HashOperations<String, Object, Object> hashOperation = redisTemplate.opsForHash();

        //1、设置键值
        hashOperation.put("hKey1","hashKey1","hash1");

        //2、设置键值，当hashkey不存在时才进行设置、返回true，hash存在时不进行设置，返回false
        Boolean result1 = hashOperation.putIfAbsent("hKey2", "hashKey1", "aaabbbccc");
        System.err.println("hashOperation.putIfAbsent(\"hKey2\", \"hashKey1\", \"aaabbbccc\") result = "+result1);
        result1 = hashOperation.putIfAbsent("hKey2", "hashKey1", "123123");
        System.err.println("hashOperation.putIfAbsent(\"hKey2\", \"hashKey1\", \"123123\") result = "+result1);
        hashOperation.put("hKey2", "hashKey2", "aabbcc");

        //3、批量设置键值
        Map<String,Object> hmap = new HashMap<>();
        hmap.put("hashKey1","hash1");
        hmap.put("hashKey2","hash2");
        hmap.put("hashKey3","hash3");
        hashOperation.putAll("hKey3",hmap);

        //4、根据key和hashkey获取值
        Object result2 = hashOperation.get("hKey3", "hashKey2");
        System.err.println("hashOperation.get(\"hKey3\", \"hashKey2\") result = "+result2);

        //5、批量根据hashkey获取值
        List<Object> result3 = hashOperation.multiGet("hKey3", Arrays.asList("hashKey1", "hashKey3"));
        System.err.println("hashOperation.multiGet(\"hKey3\", Arrays.asList(\"hashKey1\", \"hashKey3\")) result = "+result3);

        //6、根据key获取所有hashkey
        Set<Object> result4 = hashOperation.keys("hKey3");
        System.err.println("hashOperation.keys(\"hKey3\") result = "+result4);

        //7、根据key获取所有值
        List<Object> result5 = hashOperation.values("hKey3");
        System.err.println("hashOperation.values(\"hKey3\") result = "+result5);

        //8、根据key获取所有hashkey和值
        Map<Object, Object> result6 = hashOperation.entries("hKey3");
        System.err.println("hashOperation.entries(\"hKey3\") result = "+result6);

        //9、根据key获取键值对数量
        Long result7 = hashOperation.size("hKey3");
        System.err.println("hashOperation.size(\"hKey3\") result = "+result7);

        //10、根据key对hashkey进行增量(整数)
        Long result8 = hashOperation.increment("hKey3", "hashKey4", 3);
        System.err.println("hashOperation.increment(\"hKey3\", \"hashKey4\", 3) result = "+result8);

        //11、根据key对hashkey进行增量(小数)
        Double result9 = hashOperation.increment("hKey3", "hashKey5", 1.2);
        System.err.println("hashOperation.increment(\"hKey3\", \"hashKey5\", 1.2) result = "+result9);

        //12、根据key和hashkey获取value的长度
        Long result10 = hashOperation.lengthOfValue("hKey2", "hashKey2");
        System.err.println("hashOperation.lengthOfValue(\"hKey2\", \"hashKey2\") result = "+result10);

        //13、根据key和hashkey进行删除
        Long result11 = hashOperation.delete("hKey1", "hashKey1");
        System.err.println("hashOperation.delete(\"hKey1\", \"hashKey1\") result = "+result11);
    }

    /**
     * 测试list数据类型
     */
    @Test
    public void testList(){
        redisTemplate.delete("listKey");
        //0、获取list类型操作对象
        ListOperations<String, Object> listOperation = redisTemplate.opsForList();

        //1、从左进行添加
        listOperation.leftPush("listKey","a");
        System.out.println("1 :"+listOperation.range("listKey",0,-1));
        //2、从左进行批量添加
        listOperation.leftPushAll("listKey","b","c","d");
        System.out.println("2 :"+listOperation.range("listKey",0,-1));
        //3、在b后面添加o
        listOperation.leftPush("listKey","b","o");
        System.out.println("3 :"+listOperation.range("listKey",0,-1));

        //4、从左进行添加（当集合中存在元素时才添加，如果集合为空则不添加）
        listOperation.leftPushIfPresent("listKey","a");
        System.out.println("4 :"+listOperation.range("listKey",0,-1));

        //5、从右进行弹出，并且从左添加至另一个集合：listKey2
        listOperation.rightPopAndLeftPush("listKey","listKey2");
        System.out.println("5 :"+listOperation.range("listKey",0,-1));

        //6、从右进行弹出
        listOperation.rightPop("listKey");
        System.out.println("6 :"+listOperation.range("listKey",0,-1));

        //7、从右进行弹出，如果没有元素，则阻塞线程等待，最长阻塞等待时长一分钟
        listOperation.rightPop("listKey",Duration.ofMinutes(1));
        System.out.println("7 :"+listOperation.range("listKey",0,-1));

        //8、从右进行弹出，并且从左添加至另一个集合listKey3，如果没有元素，则阻塞线程等待，最长阻塞等待时长一分钟
        listOperation.rightPopAndLeftPush("listKey","listKey3",Duration.ofMinutes(1));
        System.out.println("8 :"+listOperation.range("listKey",0,-1));

        //9、获取角标1上的元素
        Object key = listOperation.index("listKey", 1);
        System.out.println("9 :"+key);

        //10、在角标1上设置元素为P
        listOperation.set("listKey",1,"p");
        System.out.println("10 :"+listOperation.range("listKey",0,-1));

        //11、在右侧添加元素
        listOperation.rightPushAll("listKey","r","s","t","r","s","t","r","s","t");
        System.out.println("11 :"+listOperation.range("listKey",0,-1));

        //12、从集合中，从左到右删除r元素，最多删除2个r
        listOperation.remove("listKey",2,"r");
        System.out.println("12 :"+listOperation.range("listKey",0,-1));

        //13、截取集合,包头包尾，会直接改变集合中的元素（未截取到的部分进行删除）
        listOperation.trim("listKey",1,3);
        System.out.println("13 :"+listOperation.range("listKey",0,-1));

        //14、获取集合长度
        Long size = listOperation.size("listKey");
        System.out.println("14 :"+size);

    }

    /**
     * 测试set数据类型
     */
    @Test
    public void testSet(){
        redisTemplate.delete("setKey1");
        redisTemplate.delete("setKey2");
        for (int i = 1; i < 5; i++) {
            redisTemplate.delete("destSetKey"+i);
        }

        //0、获取set类型操作对象
        SetOperations<String, Object> setOperation = redisTemplate.opsForSet();

        //1、添加元素至集合
        Long num1 = setOperation.add("setKey1", "a", "b", "c", "d", "e", "f");
        System.out.println("1: "+setOperation.members("setKey1"));

        //2、判断某元素是否在集合中
        Boolean isMember = setOperation.isMember("setKey1", "b");
        System.out.println("2: "+isMember);

        //3、获取集合元素数量
        Long size = setOperation.size("setKey1");
        System.out.println("3: "+size);

        //4、获取集合中所有元素
        Set<Object> members = setOperation.members("setKey1");
        System.out.println("4: "+members);

        //5、随机获取一个元素
        Object randomMember = setOperation.randomMember("setKey1");
        System.out.println("5: "+randomMember);

        //6、随机获取多个元素(有可能重复)
        List<Object> randomMembers = setOperation.randomMembers("setKey1", 3);
        System.out.println("6: "+randomMembers);

        //7、添加元素至集合
        Long num2 = setOperation.add("setKey2", "a", "b", "c", "x", "y", "z");
        System.out.println("7: "+setOperation.members("setKey2"));

        //8、获取两个集合的并集
        Set<Object> union = setOperation.union("setKey1", "setKey2");
        System.out.println("8: "+union);

        //9、获取两个集合的并集，同时存储在另一个集合
        Long num3 = setOperation.unionAndStore("setKey1", "setKey2", "destSetKey1");
        System.out.println("9: "+num3);
        System.out.println("9: "+setOperation.members("destSetKey1"));

        //10、获取两个集合的差集（查出左侧集合 中 在右侧集合中不存在的元素）
        Set<Object> difference = setOperation.difference("setKey1", "setKey2");
        System.out.println("10: "+difference);

        //11、获取两个集合的差集（查出左侧集合 中 在右侧集合中不存在的元素），同时存储在另一个集合
        Long num4 = setOperation.differenceAndStore("setKey1", "setKey2", "destSetKey2");
        System.out.println("11: "+num4);
        System.out.println("11: "+setOperation.members("destSetKey2"));

        //12、获取两个集合的交集
        Set<Object> intersect = setOperation.intersect("setKey1", "setKey2");
        System.out.println("12: "+intersect);

        //13、获取两个集合的交集，同时存储在另一个集合
        Long num5 = setOperation.intersectAndStore("setKey1", "setKey2", "destSetKey3");
        System.out.println("13: "+num5);
        System.out.println("13: "+setOperation.members("destSetKey3"));

        //14、随机删除并且返回一个元素
        Object obj = setOperation.pop("setKey2");
        System.out.println("14: "+obj);
        System.out.println("14: "+setOperation.members("setKey2"));

        //15、从集合中删除元素
        Long remove = setOperation.remove("setKey2", "x", "y", "z");
        System.out.println("15: "+remove);
        System.out.println("15: "+setOperation.members("setKey2"));

        //16、从集合中移动元素至另一个集合（原集合删除，目标集合添加）
        Boolean move = setOperation.move("setKey2", "a", "destSetKey4");
        System.out.println("16: "+move);
        System.out.println("16: "+setOperation.members("setKey2"));
        System.out.println("16: "+setOperation.members("destSetKey4"));

        setOperation.distinctRandomMembers("setKey2",4);

        //遍历集合（和iterator一模一样）
        Cursor<Object> scan = setOperation.scan("test3", ScanOptions.NONE);
        while (scan.hasNext()){
            Object next = scan.next();
            System.out.println(next);
        }
    }

    /**
     * 测试zset数据类型
     */
    @Test
    public void testZset(){
        ZSetOperations<String, Object> zsetOperation = redisTemplate.opsForZSet();

        //添加元素(有序集合是按照元素的score值由小到大进行排列)
        zsetOperation.add("key", "value", 12.1);

        //删除对应的value,value可以为多个值
        zsetOperation.remove("key", "values");

        //增加元素的score值，并返回增加后的值
        zsetOperation.incrementScore("key", "value", 23.1);

        //返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
        zsetOperation.rank("key", "value");

        //返回元素在集合的排名,按元素的score值由大到小排列
        zsetOperation.reverseRank("key", "value");

        //获取集合中给定区间的元素(start 开始位置，end 结束位置, -1查询所有)
        zsetOperation.reverseRangeWithScores("key", 2,5);

        //按照Score值查询集合中的元素，结果从小到大排序
        zsetOperation.reverseRangeByScore("key", 1.2, 12.32);
        zsetOperation.reverseRangeByScoreWithScores("key", 1.2, 12.32);

        //返回值为:Set<ZSetOperations.TypedTuple<V>>
        //从高到低的排序集中获取分数在最小和最大值之间的元素
        zsetOperation.reverseRangeByScore("key", 1.2, 12.32, 2, 5);

        //根据score值获取集合元素数量
        zsetOperation.count("key", 1.4, 12.4);

        //获取集合的大小
        zsetOperation.size("key");
        zsetOperation.zCard("key");

        //获取集合中key、value元素对应的score值
        zsetOperation.score("key", "value");

        //移除指定索引位置处的成员
        zsetOperation.removeRange("key", 2, 6);

        //移除指定score范围的集合成员
        zsetOperation.removeRangeByScore("key", 1.6, 92.23);

        //获取key和otherKey的并集并存储在destKey中（其中otherKeys可以为单个字符串或者字符串集合）
        zsetOperation.unionAndStore("key", "otherKey", "destKey");

        //获取key和otherKey的交集并存储在destKey中（其中otherKeys可以为单个字符串或者字符串集合）
        zsetOperation.intersectAndStore("key", "otherKey", "destKey");

        //遍历集合（和iterator一模一样）
        Cursor<ZSetOperations.TypedTuple<Object>> scan = zsetOperation.scan("test3", ScanOptions.NONE);
        while (scan.hasNext()){
            ZSetOperations.TypedTuple<Object> item = scan.next();
            System.out.println(item.getValue() + ":" + item.getScore());
        }
    }

    /**
     * 测试redis公共方法
     */
    @Test
    public void testCommonMethod(){
        //1、根据key获取value类型
        redisTemplate.type("setKey1");
        //2、根据key删除或批量删除
        redisTemplate.delete("strKey1");
        //3、根据key设置过期时间
        redisTemplate.expire("strKey1",1,TimeUnit.MINUTES);
        //4、根据key获取过期时间
        redisTemplate.getExpire("strKey1");
        //5、查询key是否存在
        redisTemplate.hasKey("strKey1");
        //6、根据匹配规则获取所有key
        redisTemplate.keys("str*");
        //7、将key重命名
        redisTemplate.rename("strKey1","strKey");
        //8、当key存在时进行重命名
        redisTemplate.renameIfAbsent("strKey1","strKe2");
        //9、将key持久化保存
        redisTemplate.persist("strKey");
        //10、将当前数据库的key移动到指定redis中数据库当中
        redisTemplate.move("strKey",2);
        //11、随机获取key
        redisTemplate.randomKey();
    }
}
