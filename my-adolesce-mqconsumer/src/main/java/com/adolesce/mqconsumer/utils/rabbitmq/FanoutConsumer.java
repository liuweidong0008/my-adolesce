package com.adolesce.mqconsumer.utils.rabbitmq;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布订阅消费端
 *
 * @author lwd
 */
public abstract class FanoutConsumer extends MqConsumer {
    /**
     * @param exchange  交换机名称
     * @param queue     队列名称
     * @param isDurable 队列、消息是否持久化
     * @throws Exception
     */
    public void receive(String exchange, String queue, boolean isDurable) throws Exception {
        Connection connection = cf.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, isDurable);
        if (StringUtils.isEmpty(queue)) {
            queue = channel.queueDeclare().getQueue();
        } else {
            Map<String, Object> poc = new HashMap<String, Object>();
            poc.put("x-ha-policy", "all");
            channel.queueDeclare(queue, isDurable, false, false, poc);
        }
        channel.queueBind(queue, exchange, "");
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                try {
                    FanoutConsumer.this.doWork(new String(body, "UTF-8"));
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
