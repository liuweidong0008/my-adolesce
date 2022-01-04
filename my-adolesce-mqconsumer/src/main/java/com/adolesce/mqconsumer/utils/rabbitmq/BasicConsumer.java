package com.adolesce.mqconsumer.utils.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础消费端
 * @author lwd
 *
 */
public abstract class BasicConsumer extends MqConsumer {
	/**
	 *
	 * @param queue	队列名称
	 * @param isDurable 队列、消息是否持久化
	 * @param isAck 是否开启手动消息应答(消息回执)
	 * @param prefetchCount 消息分发条数
	 * @throws Exception
	 */
	public void receive(String queue, boolean isDurable,final boolean isAck, int prefetchCount) throws Exception {
		Connection connection = cf.newConnection();
		final Channel channel = connection.createChannel();
		//指定死信发送的Exchange
		Map<String, Object> agruments = new HashMap<String, Object>();
		agruments.put("x-dead-letter-exchange", "dlx.exchange");
		channel.queueDeclare(queue, isDurable, false, false, agruments);

		//要进行死信交换机和死信队列的声明
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
		channel.queueDeclare("dlx.queue", true, false, false, null);
		channel.queueBind("dlx.queue", "dlx.exchange", "#");

		//负载均衡参数：指定mq一次推给消费者多少条消息，如果不指定，将一次性平均分给所有消费者
		channel.basicQos(prefetchCount);
		Consumer consumer = new DefaultConsumer(channel) {
			/*回调方法，当收到消息后，会自动执行该方法
                1. consumerTag：标识
                2. envelope：获取一些信息，交换机，路由key...
				3. properties:配置信息
                4. body：数据
            */
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
									   byte[] body) throws IOException {
				try {
					BasicConsumer.this.doWork(new String(body, "UTF-8"));
					if(isAck) {
						//开启手动确认（当关闭自动确认后，必须开启手动确认，否则消息会一直存在于mq中）
						//deliveryTag:该消息的index
						//multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息。
						channel.basicAck(envelope.getDeliveryTag(), false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(isAck) {
						//deliveryTag:该消息的index
						//requeue：被拒绝的是否重新入队列
						channel.basicReject(envelope.getDeliveryTag(), true);
					}
				}
			}
		};
		/*1. queue：队列名称
		  2. autoAck：是否开启自动确认（如果true，表示开启自动确认，如果false，表示为关闭自动确认）
		  3. callback：回调对象*/
		channel.basicConsume(queue,!isAck, consumer);
	}

	public abstract void doWork(String paramString) throws Exception;
}
