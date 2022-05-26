package com.adolesce.server;

import com.adolesce.cloud.dubbo.api.db.MpUserApi;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: Dubbo远程调用测试
 * @date 2021/9/6 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DubboRemoteCallTest {
    @DubboReference
    private MpUserApi mpUserApi;

    @Test
    public void test(){
        List<MpUser> users = mpUserApi.queryByNameCustom("赵晓雅");
        MpUser user = mpUserApi.getById(1L);
        System.err.println(users);
        System.err.println(user);

        QueryWrapper queryWrapper = new QueryWrapper();
        mpUserApi.list(queryWrapper);
    }
}
