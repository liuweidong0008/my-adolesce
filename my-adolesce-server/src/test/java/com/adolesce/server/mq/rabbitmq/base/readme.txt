一、如何保证消息不丢失
    1、生产消息发送到rabbitmq过程中不丢失消息
        1）、springboot集成，开启rabbimq的事务 或者 开启rabbitmq的confirm回调确认机制(常用。确认消息已经发送到rabbitmq且持久化了再进行回调)
             1.1）、生产方增加配置：spring.rabbitmq.publisher-confirm-type = correlated
             1.2）、为rabbitmqTemplate设置confirm回调：rabbitTemplate.setConfirmCallback(new ConfirmCallbackService());

    2、消息已经发送到rabbitmq不丢失消息
        rabbitmq消息持久化(即使rabbitmq宕机了，也不会丢失，重启后会从磁盘加载消息自动路由给消费者进行消费)
        1）、消息队列queue持久化
            channel.queueDeclare(queue, isDurable, false, false, null);
        2）、交换机exchange持久化
            channel.exchangeDeclare(exchange, "fanout", isDurable);
        3）、消息message持久化
            方式一：
            channel.basicPublish(exchange, "",isDurable?MessageProperties.PERSISTENT_TEXT_PLAIN:null,message.getBytes("UTF-8"));
            方式二：
            AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder();
                        properties.deliveryMode(2);  // 设置消息是否持久化，1： 非持久化 2：持久化
            channel.basicPublish(exchange, "",isDurable?properties.build():null,message.getBytes("UTF-8"));
        4）、注意：springboot集成rabbitmq，以上三者默认都已经开启了持久化，无需特殊配置
                备注：显示的设置消息持久化：message -> {
                                          //设置该条消息持久化
                                          message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                                          return message;
                                       }

    3、消息路由到消费者消费时不丢失消息
        1）、关闭自动应答(不管是否出现异常都会先进行应答，应该在消费成功后再应答)，开启手动应答模式。如果关闭自动应答，务必在消费完成后进行手动应答，否则会造成mq中消息堆积
            1.1)、消费方增加开启手动的配置：spring.rabbitmq.listener.direct.acknowledge-mode = manual
                 消息消费确认模式有：
                    AcknowledgeMode.NONE：自动确认，consumer收到消息后自动将相应的message从RabbitMQ中移除
                    AcknowledgeMode.AUTO：根据情况确认
                    AcknowledgeMode.MANUAL：手动确认，调用channel.basicAck()手动签收
            1.2)、消费完消息，进行手动ACK
                 try {
                    //业务处理代码
                    System.err.println("work-queue队列消费者1 消费消息：" + user);
                    //手动ACK
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                 } catch (Exception e) {
                    if (message.getMessageProperties().getRedelivered()) {
                        log.error("消息已重复处理失败,拒绝再次接收...", e);
                        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
                    } else {
                        log.error("消息即将再次返回队列处理...", e);
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                 }


 二、如何保证消息的幂等性


 三、如何保证消息的顺序消费

 四、rabbitmq死信队列
    https://www.cnblogs.com/mfrank/p/11184929.html
    https://blog.csdn.net/fan521dan/article/details/104912794

 五、rabbitmq延时队列
    https://www.cnblogs.com/jwen1994/p/14377549.html
    https://www.cnblogs.com/mfrank/p/11260355.html
    https://www.cnblogs.com/haixiang/p/10966985.html
    在 RabbitMQ 3.6.x 之前我们一般采用死信队列+TTL过期时间来实现延迟队列，我们这里不做过多介绍，可以参考之前文章来了解：TTL、死信队列
    在 RabbitMQ 3.6.x 开始，RabbitMQ 官方提供了延迟队列的插件，可以下载放置到 RabbitMQ 根目录下的 plugins 下。延迟队列插件下载
