package controller;

/**
 * Created by Maxime on 19/11/2015.
 */

import com.rabbitmq.client.Channel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Administrator;

public class Sender extends Amqp{

    private Administrator theAdministrator;
    private ArrayList<String> messages;
    private Channel channel;

    public Sender(Administrator anAdministrator){

        messages = new ArrayList<String>();
        File fileKey = new File("C:\\Users\\Maxime\\Documents\\NetBeansProjects\\CollectorsAdministrator\\src\\ressources\\config-keygen.json");
        File fileColl = new File("C:\\Users\\Maxime\\Documents\\NetBeansProjects\\CollectorsAdministrator\\src\\ressources\\config-collgen.json");

        String aString;
        String anotherString;
        try {
            aString = new Scanner(fileKey).useDelimiter("\\Z").next();
            anotherString = new Scanner(fileColl).useDelimiter("\\Z").next();

            messages.add(aString);
            messages.add(anotherString);
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException : " + ex.getMessage());
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            channel = connect();
        } catch (Exception ex) {
            System.err.println("Exception : " + ex.getMessage());
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }

        String message;

        for(int moreMessage=0;moreMessage<64;moreMessage++){
            message = "{\"request\":{\"type\":\"request\",\"target\":\"sum\",\"identifier\":\"id\",\"region\":\"euw\",\"data\":[\n";
            for(int i = 1;i<=4;i++){
                int randomNumber = 10000000 + (int)(Math.random()*25000000);
                message = message + "{"
                        + "\"rank\":\""+i+"\","
                        + "\"ident\":\""+randomNumber+"\""
                        + "}";
                if(i<19){
                    message = message + ",\n";
                }
            }
            message = message + "\n]\n}\n}";
            messages.add(message);
        }
    }

    public void testSend(){
        for(String aMessage : messages){
            try {
                System.out.println("Tentative d'envoi du message ...");
                channel.basicPublish("", QUEUE_NAME, null, aMessage.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/*
        {
            "id":"0",
            "value":"c0a4a6d5-d0b6-4c15-aaef-e9e8dadb3dd7",
            "rateLimit":"1255"
        },
        {
            "id":"1",
            "value":"37c74a4c-6e96-4aea-bcb9-78e19637e846",
            "rateLimit":"1255"
        },
        {
            "id":"2",
            "value":"a3c7934a-e165-48b7-99d7-ed36a62d1a00",
            "rateLimit":"1255"
        },
        {
            "id":"3",
            "value":"5b1199a3-a338-45c1-a554-d17ae1b44533",
            "rateLimit":"1255"
        },
        {
            "id":"4",
            "value":"ffd157f7-3a9a-437e-8408-5df24c63546c",
            "rateLimit":"1255"
        }
*/