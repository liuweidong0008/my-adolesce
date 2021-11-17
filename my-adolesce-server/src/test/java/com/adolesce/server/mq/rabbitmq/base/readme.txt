一、如何保证消息不丢失
    1、生产消息发送到rabbitmq过程中不丢失消息
        1）、开启rabbimq的事务 或者 开启rabbitmq的confirm回调确认机制

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

    3、消息路由到消费者消费时不丢失消息
        1）、关闭自动应答(不管是否出现异常都会先进行应答，应该在消费成功后再应答)，开启手动应答模式。

 二、如何保证消息的幂等性


 三、如何保证消息的顺序消费