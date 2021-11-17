package com.adolesce.server.utils.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 基础生产端
 * @author lwd
 *
 */
public class BasicSender extends MqSender {
	/**
	 *
	 * @param queue	队列名称
	 * @param message 消息
	 * @param isDurable 队列、消息是否持久化(true：是  false:否)
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void send(String queue,String message,boolean isDurable) throws IOException, TimeoutException {
		//创建连接工厂
		Connection connection = cf.newConnection();
		//创建通道
		Channel channel = connection.createChannel();

	    /*
	  	创建声明
	  	1). queue：队列名称，如果没有一个名字叫queue的队列，则会创建该队列，如果有则不会创建
		2). durable:是否持久化，当mq重启之后，还在
		3). exclusive：
				* 是否私有独占。只能有一个消费者监听这队列
				* 当Connection关闭时，是否删除队列
		4). autoDelete:是否自动删除。当没有Consumer时，自动删除掉
		5). arguments：参数。
		*/
		channel.queueDeclare(queue, isDurable, false, false, null);

		/*
		发送消息
		1. exchange：交换机名称。简单模式下交换机会使用默认的 ""
		2. routingKey：路由键名称
		3. props：配置信息
			该参数设置消息持久化两种方式
			1）、AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder();
				properties.deliveryMode(2);  // 设置消息是否持久化，1： 非持久化 2：持久化
				properties.build();
			2）、MessageProperties.PERSISTENT_TEXT_PLAIN
		4. body：发送消息数据
		*/
		channel.basicPublish("", queue, isDurable?MessageProperties.PERSISTENT_TEXT_PLAIN:null, message.getBytes("UTF-8"));
		//释放资源
		channel.close();
		connection.close();
	}
}
