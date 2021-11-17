package com.adolesce.mqconsumer.utils.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 路由消费端
 *
 * @author lwd
 */
public abstract class DirectRoutingConsumer extends MqConsumer {
    /**
     * @param exchange   交换机
     * @param queue      队列
     * @param bindingKey
     * @param isDurable  队列、消息是否持久化(true：是  false:否)
     * @throws IOException
     * @throws TimeoutException
     */
    public void receive(String exchange, String queue, String[] bindingKey, boolean isDurable) throws Exception {
        Connection connection = cf.newConnection();
        final Channel channel = connection.createChannel();
        /*参数：
        1. exchange:交换机名称
        2. type:交换机类型
              DIRECT("direct"),：定向
              FANOUT("fanout"),：扇形（广播），发送消息到每一个与之绑定队列。
              TOPIC("topic"),通配符的方式
              HEADERS("headers");参数匹配
        3. durable:是否持久化
        4. autoDelete:自动删除
        5. internal：内部使用。 一般false
        6. arguments：参数*/
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, isDurable,false,false,null);
        Map<String, Object> poc = new HashMap<String, Object>();
        poc.put("x-ha-policy", "all");
        channel.queueDeclare(queue, isDurable, false, false, poc);
        //绑定队列和交换机
        for (String severity : bindingKey) {
            channel.queueBind(queue, exchange, severity);
        }
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                try {
                    DirectRoutingConsumer.this.doWork(new String(body, "UTF-8"));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    channel.basicReject(envelope.getDeliveryTag(), true);
                }
            }
        };
        Map<String, Object> policys = new HashMap<String, Object>();

        policys.put("x-cancel-on-ha-failover", Boolean.valueOf(true));
        channel.basicConsume(queue, false, policys, consumer);
    }

    public abstract void doWork(String paramString) throws Exception;
}