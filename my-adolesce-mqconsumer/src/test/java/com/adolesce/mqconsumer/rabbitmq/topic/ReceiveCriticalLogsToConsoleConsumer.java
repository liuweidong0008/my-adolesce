package com.adolesce.mqconsumer.rabbitmq.topic;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.TopicConsumer;

/**
 * 接收与*critical匹配的消息
 * @author lwd
 */
public class ReceiveCriticalLogsToConsoleConsumer extends TopicConsumer {
	@Override
	public void doWork(String message) {
		 System.out.println(" [x] Received *.critical:"   
                 + ",msg = " + message + ".");  
	}

	public static void main(String[] args) {
		RabbitFactory.init();
		String [] bingdingkeys = {"*.critical"};
		ReceiveCriticalLogsToConsoleConsumer rlc = new ReceiveCriticalLogsToConsoleConsumer();
		try {
			//log-topic-exchange交换机消费者1
			rlc.receive("log-topic-exchange","log-topic-queue1",bingdingkeys,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
