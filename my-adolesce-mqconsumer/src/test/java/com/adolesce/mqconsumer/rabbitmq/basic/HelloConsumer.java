package com.adolesce.mqconsumer.rabbitmq.basic;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.BasicConsumer;

import java.util.concurrent.*;

/**
 * 消费者（接收）
 * @author Administrator
 *
 */
public class HelloConsumer extends BasicConsumer {
	//public ExecutorService executor = Executors.newFixedThreadPool(8); // 创建的线程池的任务队列是无边界的：
/*	public static ExecutorService newFixedThreadPool(int nThreads) {
	     return new ThreadPoolExecutor(nThreads, nThreads,
	                                          0L, TimeUnit.MILLISECONDS,
	                                          new LinkedBlockingQueue<Runnable>());
	}*/
	
	
	//消息不能不受控地进入线程池的任务队列，所以，要换成使用定长的阻塞队列，队列满了就暂停拉取消息。把线程池替换成：
	private int nThreads = 8;
	private int MAX_QUEUQ_SIZE = 2000;
	private ExecutorService executor = new ThreadPoolExecutor(nThreads,
	          nThreads, 0L, TimeUnit.MILLISECONDS,
	        //  new ArrayBlockingQueue<Runnable>(MAX_QUEUQ_SIZE),
	          new LinkedBlockingQueue<Runnable>(MAX_QUEUQ_SIZE),
	          new ThreadPoolExecutor.CallerRunsPolicy());
		
/*	线程池队列满的时候直接让调用者（也就是Consumer）执行任务，这样就延缓了消息拉取的速度，
             当 Consumer 再去拉取消息时，发现线程池有空间时可以提交到线程池，让线程池的工作线程去处理，它继续保持拉取速度。
	这样既控制了线程池占用的内存，又可以让消息处理线程池处理不过来时多一个线程处理消息。*/
	
	
	
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		System.out.println(" [x] Received '" + message + "'");  
		
		 executor.execute(new Runnable() {
             @Override
             public void run() {
                  // 耗时且复杂的消息处理逻辑
                  complicateHanlde(message);
             }
        });
	}  
	
    private void complicateHanlde(Object message) {
    	System.out.println("消费者中异步任务执行....");
    }


	public static void main(String[] args) {
		RabbitFactory.init();
		HelloConsumer hc = new HelloConsumer();
		try {
			hc.receive("hello-queue",true, true, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
