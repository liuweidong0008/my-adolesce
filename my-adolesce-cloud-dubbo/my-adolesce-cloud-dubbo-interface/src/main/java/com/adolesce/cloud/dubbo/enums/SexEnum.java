package com.adolesce.cloud.dubbo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 1、编写符合条件的枚举类，方式有两种
 *      1）、枚举类实现IEnum接口重写getValue方法
 *      2）、在相应字段上打上@EnumValue注解
 * 2、配置文件指定扫描枚举，方式有两种
 *      1）、扫描局部指定枚举包
 *             mybatis-plus:
 *                  type-enums-package: com.adolesce.cloud.dubbo.enums
 *      2）、全局配置，直接指定 DefaultEnumTypeHandler
 *             mybatis-plus:
 *                  # 修改 mybatis 的 DefaultEnumTypeHandler
 *                  configuration:
 *                      default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
 *
 * 3、原理：底层会将符合上述条件的枚举，注册使用MybatisEnumTypeHandler进行处理
 */
public enum SexEnum /*implements IEnum<Integer> */{

    MAN(1, "男"),
    WOMAN(2, "女"),
    UNKNOWN(3, "未知");

    @EnumValue
    private int value;
    private String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /*@Override
    public Integer getValue() {
        return this.value;
    }*/

    @Override
    public String toString() {
        return this.desc;
    }
}
