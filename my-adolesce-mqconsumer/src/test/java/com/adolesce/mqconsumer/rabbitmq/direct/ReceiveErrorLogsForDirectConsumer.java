package com.adolesce.mqconsumer.rabbitmq.direct;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.DirectRoutingConsumer;

/**
 * 消费者（接收）接收error
 * @author Administrator
 *
 */
public class ReceiveErrorLogsForDirectConsumer extends DirectRoutingConsumer {
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		System.out.println(" [error] start: '" + message + "'");  
		doWorkStart();
		System.out.println(" [error] end: '" + message + "'");  
	}  
	
	/** 
     * 耗时3s 
     * @param 
     * @throws InterruptedException 
     */  
    private void doWorkStart()
    {  
        try {
        	Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }

	public static void main(String[] args) {
		RabbitFactory.init();
		String [] bingdingkeys = {"error"};
		ReceiveErrorLogsForDirectConsumer rle = new ReceiveErrorLogsForDirectConsumer();
		try {
			//log-direct-exchange交换机消费者3
			rle.receive("log-direct-exchange", "log-direct-queue-error", bingdingkeys,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

