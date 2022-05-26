package com.adolesce.common.utils.ids;

/**
 * ID生成器工具类
 * 
 * 
 */
public class IdUtils
{
    /**
     * 获取随机UUID
     * 
     * @return 随机UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 随机UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 用Random UUID 基于16进制 取模得出的UUID（8位）
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String HX16UUID()
    {
        return UUID.getHX16UUID();
    }

    public static String snowFlakeId() {
        return String.valueOf(SnowFlakeIdUtils.getSnowFlakeId());
    }

    public static void main(String[] args) {
        System.out.println(snowFlakeId());
    }
}

