package com.adolesce.server.mutithread;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/8/14 16:44
 */
public class Ticket implements Runnable {
    private Integer ticket;
    private static ThreadLocal<Integer> tl = new ThreadLocal<>();

    public Ticket(Integer ticket) {
        this.ticket = ticket;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (ticket) {
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + "在卖第" + (100 - (--ticket)) + "张票 " + tl);
                }
            }
            try {
                if (ticket == 50) {
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
