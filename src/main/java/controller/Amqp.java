package controller;

/**
 * Created by Maxime on 19/11/2015.
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class Amqp {

    protected final static String QUEUE_NAME = "testCollectorBigData";

    protected static Channel connect()throws Exception{

        //env : amqp://zivlmqtn:PQY2WJQ1pWumqr4E_VV2YHYugidvt_0G@spotted-monkey.rmq.cloudamqp.com/zivlmqtn;zivlmqtn;PQY2WJQ1pWumqr4E_VV2YHYugidvt_0G
        //get variable
        String uri = System.getenv("cloud_amqp").split(";")[0];
        String username = System.getenv("cloud_amqp").split(";")[1];
        String password = System.getenv("cloud_amqp").split(";")[2];

        //initialize channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        factory.setUsername(username);
        factory.setPassword(password);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        return channel;
    }
}
