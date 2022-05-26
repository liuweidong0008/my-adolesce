一、如何保证消息不丢失
    1、生产消息发送到rabbitmq过程中不丢失消息
        1）、可以开启rabbitmq的事务 或者 开启rabbitmq的回调确认机制(常用)
             1.1）、生产方增加配置(开启)：
                spring.rabbitmq.publisher-confirm-type = correlated  #开启confirm机制：消息投递到交换机时回调【成功或失败都会回调】(- `simple`：同步等待confirm结果，直到超时 - `correlated`：异步回调，定义ConfirmCallback，MQ返回结果时会回调这个ConfirmCallback)
                spring.rabbitmq.publisher-returns: true  #开启returns机制：消息从交换机未路由到队列时回调（同样是基于callback机制，不过是定义ReturnCallback）
                spring.rabbitmq.template.mandatory: true #定义returns机制消息路由失败时的策略【true：进行return回调；false：直接丢弃消息】

             1.2）、生产方增加代码配置(为rabbitmqTemplate设置confirm回调)
                1、rabbitTemplate.setConfirmCallback(new ConfirmCallbackService());  //可全局设置，也可以每次发消息时进行设置，两者可共存（发消息时设置的优先执行）
                2、rabbitTemplate.setReturnCallback(new ReturnCallBackHandler());    //要求为单例，全局设置一次

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
                备注：显示的设置消息持久化：
                    方式一：
                    message -> {
                      //设置该条消息持久化
                      message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                      return message;
                    }
                    方式二：
                    Message message2 = MessageBuilder.withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
                                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                                        .build();

    3、消息路由到消费者消费时不丢失消息
        1)、消费方增加消息确认的配置：spring.rabbitmq.listener.direct.acknowledge-mode = manual
        2)、消息消费确认模式有：
            AcknowledgeMode.NONE：【关闭ACK，MQ假定消费者获取消息者后会成功处理，因此消息投递后会立即被删除；
            AcknowledgeMode.MANUAL：手动ACK，需要在业务代码后，调用API手动发送ACK进行签收；
            AcknowledgeMode.AUTO：自动ACK，由spring检测listener代码是否出现异常，没有异常则返回ack，抛出异常则返回nack，消息会被无限重复消费（原理为AOP）

            1、如果模式为NONE，MQ会假定消费者获取消息后会成功处理，因此消息投递后会立即被删除
            2、如果模式为MANUAL，回执完全由编码者进行控制，消费完消息，需要进行手动ACK(务必在消费完成后进行手动应答，否则会造成mq中消息堆积)
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
            3、如果模式为AUTO，回执是自动完成的：
                1）、如果没有出现异常，则给mq回执ack，消息从mq中删除；
                2）、如果出现异常，则给mq回执nack，消息会被mq重新投递至队列进而被再次消费。【如果消费者代码有问题，消息就会被mq进行无限重复投递，这样会对mq造成很大压力，所以此时要结合使用消费者重试机制】
                     消费者重试机制：【原理是spring的retry机制，在消费者出现异常时利用本地重试，而不是无限次的requeue到mq队列】
                     1、消费者端开启本地重试
                        spring:
                          rabbitmq:
                            listener:
                              simple:
                                retry:
                                  enabled: true # 开启消费者失败重试
                                  initial-interval: 1000 # 初始的失败等待时长为1秒
                                  multiplier: 1 # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval
                                  max-attempts: 3 # 最大重试次数
                                  stateless: true # true无状态；false有状态。如果业务中包含事务，这里改为false
                     - 开启本地重试时，消息处理过程中抛出异常，不会requeue到队列，而是在消费者本地重试
                     - 重试达到最大次数后，Spring会返回ack，消息会被丢弃(默认)
                     - 重试次数用完后，失败策略有三种：【需要通过MessageRecoverer接口来处理，包含三种不同的实现】
                           - RejectAndDontRequeueRecoverer：重试耗尽后，直接reject，丢弃消息。默认就是这种方式
                           - ImmediateRequeueMessageRecoverer：重试耗尽后，返回nack，消息重新入队
                           - RepublishMessageRecoverer：重试耗尽后，将失败消息投递到指定的交换机
                           比较优雅的一种处理方案是RepublishMessageRecoverer，失败后将消息投递到一个指定的，专门存放异常消息的队列，后续由人工集中处理。
                               定义一个RepublishMessageRecoverer，关联队列和交换机：
                                @Bean
                                public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
                                    return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
                                }

    4、总结：如何确保RabbitMQ消息的可靠性？
        - 开启生产者确认机制，确保生产者的消息能到达队列
        - 开启持久化功能，确保消息未消费前在队列中不会丢失
        - 开启消费者确认机制为auto，由spring确认消息处理成功后完成ack
        - 开启消费者失败重试机制，并设置MessageRecoverer，多次重试失败后将消息投递到异常交换机，交由人工处理

 二、如何保证消息的幂等性
       发送的消息中要携带一个唯一性ID，消费代码中要根据ID进行判断，如果消费过该消息，就不消费了

 三、rabbitmq死信队列
    https://www.cnblogs.com/mfrank/p/11184929.html
    https://blog.csdn.net/fan521dan/article/details/104912794
    1、什么是死信？
        当一个队列中的消息满足下列情况之一时，可以成为死信（dead letter）：
        - 消费者使用basic.reject或 basic.nack声明消费失败，并且消息的requeue参数设置为false
        - 消息是一个过期消息，超时无人消费
        - 要投递的队列消息满了，无法投递

    2、什么是死信交换机？
        - 如果这个包含死信消息的队列配置了`dead-letter-exchange`属性，指定了一个交换机，那么队列中的死信就会投递到这个交换机中，而这个交换机就被称为死信交换机（Dead Letter Exchange，检查DLX）。

 四、rabbitmq延时消息
     https://www.cnblogs.com/jwen1994/p/14377549.html
     https://www.cnblogs.com/mfrank/p/11260355.html
     https://www.cnblogs.com/haixiang/p/10966985.html
    1、什么是ttl?
        time-to-live。如果一个队列中的消息TTL结束仍未被消费，则会变成死信，ttl超时分为两种情况
        1）、给消息所在的队列设置了消息存活时间，消息进入队列后超过ttl时间会变为死信；
        2）、给消息本身设置了存活时间，超过ttl时间未被消费会变为死信。
        注意：如果上述两种方式都配置了，以时间短的为准
    2、TTL + 死信队列 实现延时消息（RabbitMQ 3.6.x 之前我们一般采用死信队列+TTL过期时间来实现延迟队列）
        1）、原理：给队列或者消息本身设置存活时间，超过指定时间消息会被投递至死信交换机，进而被路由至死信队列，最后被消费者消费
        2）、实现步骤：
            1、声明一组死信交换机【dl.direct】和队列【dl.queue】
            2、声明一组普通交换机【ttl.direct】和队列【ttl.queue】，给队列设置消息存活时间，且设置死信交换机【dl.direct】和死信routingKey【dl】
            3、生产端发送消息
    3、利用DelayExchange延时插件实现 延时消息（RabbitMQ 3.6.x 开始，RabbitMQ 官方提供了延迟队列的插件）
        1）、安装DelayExchange插件
            1、插件下载地址：https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases
            2、.ez后缀的插件下载下来后放入rabbitmq安装目录的plugins目录中
            3、在mq的sbin目录下cmd执行命令：rabbitmq-plugins enable rabbitmq_delayed_message_exchange 来开启延时消息插件
            4、这样在mq控制台中创建交换机就能看见type有 x-delayed-message 类型了（在arguments中指定 x-delayed-type=direct指定交换机原有类型）。在mq控制台中向交换机中发消息时指定Header（x-delay=5000）来指定消息的延迟时间
        2）、编码实现步骤
            1、定义一组delay交换机和队列，指定交换机为具有delay特性的交换机
            2、发消息时通过header中的x-delay属性指定消息的延时时间

 五、如何解决消息堆积问题（思路）
    1、队列上绑定多个消费者，提高消费速度
    2、给消费者开启线程池，提高消费速度
    3、使用惰性队列，可以在mq中保存更多消息
        1）、通过命令行将一个运行中的队列修改为惰性队列：
            rabbitmqctl set_policy Lazy "^lazy-queue$" '{"queue-mode":"lazy"}' --apply-to queues
            - `rabbitmqctl` ：RabbitMQ的命令行工具
            - `set_policy` ：添加一个策略
            - `Lazy` ：策略名称，可以自定义
            - `"^lazy-queue$"` ：用正则表达式匹配队列的名字
            - `'{"queue-mode":"lazy"}'` ：设置队列模式为lazy模式
            - `--apply-to queues  `：策略的作用对象，是所有的队列
        2）、创建队列时声明队列为惰性队列
            1、基于@Bean声明lazy-queue
            2、基于@RabbitListener声明LazyQueue

 六、如何保证消息的顺序消费