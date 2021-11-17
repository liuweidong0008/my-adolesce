package com.adolesce.server.mutithread;

import java.util.concurrent.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/8/14 16:45
 */
public class ThreadTest {
    public static void main(String[] args) {
        Ticket ticket = new Ticket(100);

        Thread thread1 = new Thread(ticket, "窗口一");
        Thread thread2 = new Thread(ticket, "窗口二");
        Thread thread3 = new Thread(ticket, "窗口三");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    /*java中创建线程池的方式一般有两种：

    通过Executors工厂方法创建
    通过new ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)自定义创建*/
    public void test1() {
        //创建使用单个线程的线程池
        ExecutorService es1 = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            es1.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "正在执行任务");
                }
            });
        }
        //创建使用固定线程数的线程池
        ExecutorService es2 = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            es2.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "正在执行任务");
                }
            });
        }
        //创建一个会根据需要创建新线程的线程池
        ExecutorService es3 = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            es3.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "正在执行任务");
                }
            });
        }
        //创建拥有固定线程数量的定时线程任务的线程池
        ScheduledExecutorService es4 = Executors.newScheduledThreadPool(2);
        System.out.println("时间：" + System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            es4.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("时间：" + System.currentTimeMillis() + "--" + Thread.currentThread().getName() + "正在执行任务");
                }
            }, 3, TimeUnit.SECONDS);
        }
        //创建只有一个线程的定时线程任务的线程池
        ScheduledExecutorService es5 = Executors.newSingleThreadScheduledExecutor();
        System.out.println("时间：" + System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            es5.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("时间：" + System.currentTimeMillis() + "--" + Thread.currentThread().getName() + "正在执行任务");
                }
            }, 3, TimeUnit.SECONDS);
        }
    }
}
