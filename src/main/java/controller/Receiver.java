package controller;

/**
 * Created by Maxime on 19/11/2015.
 */

import java.io.IOException;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Administrator;
import org.json.JSONException;

public class Receiver extends Amqp{

    private Administrator theAdministrator;

    public Receiver(Administrator anAdministrator){
        theAdministrator = anAdministrator;
        try {
            Channel channel = Amqp.connect();
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    try {
                        theAdministrator.taskReceived(message);
                    } catch (JSONException ex) {
                        System.err.println("JSONException : " + ex.getMessage());
                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Reception d'un message (HandleDelivery)");
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
