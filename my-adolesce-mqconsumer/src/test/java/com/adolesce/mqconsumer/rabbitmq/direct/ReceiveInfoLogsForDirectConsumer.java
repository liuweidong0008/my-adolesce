package com.adolesce.mqconsumer.rabbitmq.direct;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.DirectRoutingConsumer;

/**
 * 消费者（接收）接收info
 * @author Administrator
 *
 */
public class ReceiveInfoLogsForDirectConsumer extends DirectRoutingConsumer {
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		System.out.println(" [info] start: '" + message + "'");  
		doWorkStart();
		System.out.println(" [info] end: '" + message + "'");  
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
		String [] bingdingkeys = {"info"};
		ReceiveInfoLogsForDirectConsumer rli = new ReceiveInfoLogsForDirectConsumer();
		try {
			//log-direct-exchange交换机消费者1
			rli.receive("log-direct-exchange", "log-direct-queue-info", bingdingkeys, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

