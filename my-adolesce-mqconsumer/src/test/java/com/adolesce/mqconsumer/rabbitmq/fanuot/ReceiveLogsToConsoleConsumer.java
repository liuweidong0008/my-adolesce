package com.adolesce.mqconsumer.rabbitmq.fanuot;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.FanoutConsumer;

/**
 * 消费者（接收）
 * @author Administrator
 *
 */
public class ReceiveLogsToConsoleConsumer extends FanoutConsumer {
    /**
     * 重写（业务逻辑定义）
     */
	@Override
	public void doWork(String message) {
		String index = message.substring(message.length()-1,message.length());
		System.out.println(" [x] 打印log "+ index + " START: '" + message + "'");  
		doWorkStart();
		System.out.println(" [x] 打印log "+ index + " END: '" + message + "'");  
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
		ReceiveLogsToConsoleConsumer rcc = new ReceiveLogsToConsoleConsumer();
		try {
			//log-fanout-exchange交换机消费者1
			//rcc.receive("log-fanout-exchange", null, true);//临时队列接收消息
			rcc.receive("log-fanout-exchange","log-fanout-queue1",true);//指定队列接收消息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
