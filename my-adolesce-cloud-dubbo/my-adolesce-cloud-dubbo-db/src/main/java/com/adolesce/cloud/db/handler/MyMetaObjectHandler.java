package com.adolesce.cloud.db.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mybatis-plus 字段自动填充Handler
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        Object created = getFieldValByName("createTime", metaObject);
        if (null == created) {
            //字段为空，可以进行填充
            //metaObject.setValue("createTime",LocalDateTime.now());
            setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }

        Object updated = getFieldValByName("updateTime", metaObject);
        if (null == updated) {
            //字段为空，可以进行填充
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        //更新数据时，直接更新字段
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
