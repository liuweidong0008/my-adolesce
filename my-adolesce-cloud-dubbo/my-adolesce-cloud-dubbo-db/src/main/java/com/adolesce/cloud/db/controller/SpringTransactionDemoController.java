package com.adolesce.cloud.db.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/4/26 12:29
 */
@RestController
@RequestMapping("transaction")
@Slf4j
public class SpringTransactionDemoController {
    @Autowired
    private SpringTransactionDemoService springTransactionDemoService;

    @PostMapping("addMpUser/{type}")
    public String addMpUser(@PathVariable("type") Integer type) {
        try {
            if (type == 1) {
                this.springTransactionDemoService.addMpUserToDB1();
            } else if (type == 2) {
                this.springTransactionDemoService.addMpUserToDB2();
            } else if (type == 3) {
                this.springTransactionDemoService.addMpUserToDB3();
            } else if (type == 4) {
                this.springTransactionDemoService.addMpUserToDB4();
            } else if (type == 5) {
                this.springTransactionDemoService.addMpUserToDB5();
            } else if (type == 6) {
                this.springTransactionDemoService.addMpUserToDB6();
            } else if (type == 7) {
                this.springTransactionDemoService.addMpUserToDB7();
            } else if (type == 8) {
                this.springTransactionDemoService.addMpUserToDB8();
            } else if (type == 9) {
                this.springTransactionDemoService.addMpUserToDB9();
            } else if (type == 10) {
                this.springTransactionDemoService.addMpUserToDB10();
            } else if (type == 11) {
                this.springTransactionDemoService.addMpUserToDB11();
            } else if (type == 12) {
                this.springTransactionDemoService.addMpUserToDB12();
            } else if (type == 13) {
                this.springTransactionDemoService.addMpUserToDB13();
            } else if (type == 14) {
                this.springTransactionDemoService.addMpUserToDB14();
            }
        } catch (Exception e) {
            log.error("保存用户信息出错：{}", e);
        }
        return "操作成功！";
    }

    /**
     * 保存用户测试方法
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/add88MpUserToDB/{num}")
    public Integer saveUser(@PathVariable("num")Integer num) {
        this.springTransactionDemoService.deleteMpUser("测试用户99");

        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    TimeUnit.MILLISECONDS.sleep(100L);
                    String addResult = this.springTransactionDemoService.add99MpUserToDB();
                    System.out.println(addResult);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            countDownLatch.countDown();
        }
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.springTransactionDemoService.selectMpUser("测试用户99").size();
    }

}
