package com.adolesce.mqconsumer.rabbitmq.basic;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.BasicConsumer;

/**
 * 消费者（接收）
 * @author Administrator
 *
 */
public class WorkQueueConsumer extends BasicConsumer {
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		System.out.println(" 消费者: [x] Received '" + message + "'");
		doWorkStart(message);
		//int i = 1/0;
        System.out.println(" [x] Done");
	}  
	
    /** 
     * 每个点耗时1s 
     * @throws InterruptedException
     */  
    private void doWorkStart(String message)
    {  
        for (char ch : message.toCharArray())  
        {  
			try {
				if (ch == '.') {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }  
    }

	public static void main(String[] args) {
		RabbitFactory.init();
		WorkQueueConsumer wc = new WorkQueueConsumer();
		try {
			//2个workqueue队列消费者
			wc.receive("work-queue", true, true, 1);
			//wc.receive("work-queue", true, true, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
