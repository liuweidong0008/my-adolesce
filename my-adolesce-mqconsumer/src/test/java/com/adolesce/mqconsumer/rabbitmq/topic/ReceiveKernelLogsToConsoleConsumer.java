package com.adolesce.mqconsumer.rabbitmq.topic;


import com.adolesce.mqconsumer.rabbitmq.RabbitFactory;
import com.adolesce.mqconsumer.utils.rabbitmq.TopicConsumer;

/**
 * 接收与kernel*匹配的消息
 * @author lwd
 */
public class ReceiveKernelLogsToConsoleConsumer extends TopicConsumer {
	@Override
	public void doWork(String message) {
		System.out.println(" [x] Received kernel.*:"   
                + ",msg = " + message + "."); 
	}

	public static void main(String[] args) {
		RabbitFactory.init();
		String [] bingdingkeys = {"kernel.*"};
		ReceiveKernelLogsToConsoleConsumer rlk = new ReceiveKernelLogsToConsoleConsumer();
		try {
			//log-topic-exchange交换机消费者1
			rlk.receive("log-topic-exchange","log-topic-queue2",bingdingkeys,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
