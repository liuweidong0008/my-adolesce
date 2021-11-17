package com.adolesce.mqconsumer.rabbitmq.fanuot;

import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.FanoutConsumer;

/**
 * 消费者（接收）
 * @author Administrator
 *
 */
public class ReceiveLogsToSaveConsumer extends FanoutConsumer {
	private int index = 0;
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		String index = message.substring(message.length()-1,message.length());
		System.out.println(" [x] 保存log "+ index + " START: '" + message + "'");  
		doWorkStart();
		System.out.println(" [x] 保存log "+ index + " END: '" + message + "'");  
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
		ReceiveLogsToSaveConsumer rsc = new ReceiveLogsToSaveConsumer();
		try {
			//log-fanout-exchange交换机消费者2
			//rsc.receive("log-fanout-exchange",null, true);//临时队列接收消息
			rsc.receive("log-fanout-exchange","log-fanout-queue2",true);//指定队列接收消息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

