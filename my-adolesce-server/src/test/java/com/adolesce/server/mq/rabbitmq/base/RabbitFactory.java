package com.adolesce.server.mq.rabbitmq.base;

import com.adolesce.server.utils.rabbitmq.MqSender;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Before;

import java.util.concurrent.Executors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/30 17:12
 */
public class RabbitFactory {
    @Before
    public void init(){
        RabbitFactory.initFactory();
    }

    public static void initFactory(){
        String host = "127.0.0.1";  //ip
        int port = 5672; //端口
        String userName = "lwd"; //用户名
        String passWord = "lwd"; //密码

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ所在主机ip或者主机名（默认值 localhost）
        factory.setHost(host);
        //设置RabbitMQ端口（默认值 5672）
        factory.setPort(port);
        //设置RabbitMQ用户名（默认 guest）
        factory.setUsername(userName);
        //设置RabbitMQ密码（默认值 guest）
        factory.setPassword(passWord);

        //此值已经在rabbitmq底层程序中设置了，只不过每次调用客户端都要调用一下 Runtime.getRuntime()...所以我就把它拿出来了，显示的设置上，节省一下效率，不做程序做重复的无用功，尤其是像这种频繁操作的处理。
        factory.setSharedExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, Executors.defaultThreadFactory()));
        //客户端和服务端的握手时间，此时间设置的过小，则容易出现concurrentTimeOut，这里默认设置500000
        factory.setConnectionTimeout(15000);
        factory.setHandshakeTimeout(500000);
        MqSender.cf = factory;
    }
}
