package com.adolesce.common.annotation;

import java.lang.annotation.*;

/**
 * 被标记为Cache的Controller接口进行缓存，其他情况不进行缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented //标记注解
public @interface Cache {

    /**
     * 缓存时间，默认为60秒
     */
    String time() default "60";

    /**
     * 组下自定义缓存key（支持EL表达式）
     */
    String key() default "";

    /**
     * 所属组（支持EL表达式）
     */
    String group() default "";
}