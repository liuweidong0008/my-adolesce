package com.adolesce.cloud.db.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/3/28 19:34
 */
public class DBTest {
    public static void main(String[] args) {
        System.out.println(IdWorker.getIdStr());

        /*long time = DateUtil.offsetMinute(new Date(), 5).getTime();
        System.out.println((time - System.currentTimeMillis())/1000);

        long time1 = DateUtils.addMinutes(new Date(), 5).getTime();
        System.out.println((time1 - System.currentTimeMillis())/1000);


        DateUtil.between(new Date(), new Date(), DateUnit.SECOND);*/
    }
}
