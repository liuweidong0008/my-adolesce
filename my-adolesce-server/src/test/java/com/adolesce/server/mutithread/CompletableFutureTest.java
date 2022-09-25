package com.adolesce.server.mutithread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/3 9:42
 */
@Slf4j
public class CompletableFutureTest {
    /**
     * @descript 使用CompletableFuture实现异步执行线程，合并返回最终结果
     */
    @Test
    public void test1() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            CompletableFuture<String> aFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return queryA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "";
            });
            //A线程执行完同步执行指定代码块 (还可以通过whenCompleteAsync去异步执行)
            aFuture.whenComplete((result, e) -> {
                if (!Objects.isNull(e)) {
                    log.error(e.toString());
                }
                System.out.println("A线程执行完毕，返回结果：" + result);
            });

            CompletableFuture<String> bFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return queryB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "";
            });
            bFuture.whenComplete((result, e) -> {
                if (!Objects.isNull(e)) {
                    log.error(e.toString());
                }
                System.out.println("B线程执行完毕，返回结果：" + result);
            });

            CompletableFuture<String> cFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return queryC();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "";
            });
            cFuture.whenComplete((result, e) -> {
                if (!Objects.isNull(e)) {
                    log.error(e.toString());
                }
                System.out.println("C线程执行完毕，返回结果：" + result);
            });


            //并行处理
            CompletableFuture.allOf(aFuture, bFuture, cFuture).join();
            //取值
            String a = aFuture.get();
            String b = bFuture.get();
            String c = cFuture.get();

            System.out.println("所有线程执行完毕，合并结果：" + a + "  " + b + "  " + c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println("耗时：" + stopWatch.getTotalTimeSeconds() + "秒");
    }

    /**
     * @descript 同步执行三个查询方法
     */
    @Test
    public void test1_1() {
        long startTime = System.currentTimeMillis();
        try {
            String a = queryA();
            String b = queryB();
            String c = queryC();
            System.out.println(a + "  " + b + "  " + c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) / 1000 + "秒");
    }

    /**
     * @descript 使用FutureTask和Callable实现异步执行线程，合并返回最终结果
     */
    @Test
    public void test1_2() throws ExecutionException, InterruptedException {
        org.apache.commons.lang3.time.StopWatch stopWatch = new org.apache.commons.lang3.time.StopWatch();
        stopWatch.start();
        Callable<String> callableA = () -> queryA();
        Callable<String> callableB = () -> queryB();
        Callable<String> callableC = () -> queryC();

        FutureTask<String> futureTaskA = new FutureTask<>(callableA);
        FutureTask<String> futureTaskB = new FutureTask<>(callableB);
        FutureTask<String> futureTaskC = new FutureTask<>(callableC);

        Thread threadA = new Thread(futureTaskA);
        Thread threadB = new Thread(futureTaskB);
        Thread threadC = new Thread(futureTaskC);

        threadA.start();
        threadB.start();
        threadC.start();

        String a = futureTaskA.get();
        String b = futureTaskB.get();
        String c = futureTaskC.get();
        System.out.println(a + "  " + b + "  " + c);

        stopWatch.stop();
        System.out.println("耗时：" + stopWatch.getTime(TimeUnit.SECONDS) + "秒");
    }


    private String queryA() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "A";
    }

    private String queryB() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "B";
    }

    private String queryC() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "C";
    }
}
