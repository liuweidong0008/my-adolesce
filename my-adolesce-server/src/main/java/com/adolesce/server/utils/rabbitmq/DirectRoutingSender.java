package com.adolesce.server.utils.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 路由生产端
 *
 * @author lwd
 */
public class DirectRoutingSender extends MqSender {
    /**
     * @param exchange   交换机
     * @param routingKey
     * @param message    消息
     * @param isDurable  队列、消息是否持久化(true：是  false:否)
     * @throws IOException
     * @throws TimeoutException
     */
    public static void send(String exchange, String routingKey, String message, boolean isDurable) throws IOException, TimeoutException {
        Connection connection = cf.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, isDurable);
        channel.basicPublish(exchange, routingKey, isDurable ? MessageProperties.PERSISTENT_TEXT_PLAIN : null, message.getBytes("UTF-8"));
        channel.close();
        connection.close();
    }
}
