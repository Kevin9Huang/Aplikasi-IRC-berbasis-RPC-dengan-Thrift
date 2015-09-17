/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Server;

import MessageServiceThrift.Message;
import MessageServiceThrift.MessageService.Iface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.sun.istack.internal.logging.Logger;
import org.apache.thrift.TException;

/**
 *
 * @author Kevin
 */
public class MessageDistributor implements Iface,Runnable {

    private final static Logger MyLog = Logger.getLogger(MessageDistributor.class);
    private BlockingQueue<Message> messageQueue;
    private List<MessageServiceClient> clients;
    
    public MessageDistributor(){
        this.messageQueue = new LinkedBlockingQueue<Message>();
        this.clients = new ArrayList<MessageServiceClient>();
    }
    
    public void addClient(MessageServiceClient client){
        clients.add(client);
        MyLog.info(String.format("Added a client at %s",client.getAddy()));
    }
    
    @Override
    public void sendMessage(Message msg) throws TException {
        messageQueue.add(msg);
        MyLog.info(String.format("Added Message to queue: %s",msg));
    }

    @Override
    public void run() {
        while(true){
            try{
                Message msg = messageQueue.take();
                Iterator<MessageServiceClient> clientItr = clients.iterator();
                while(clientItr.hasNext()){
                    MessageServiceClient client = clientItr.next();
                    try {
                        client.sendMessage(msg);
                    } catch (TException te) {
                        clientItr.remove();
                        MyLog.info(String.format("Removing %s from client list",client.getAddy()));
                        MyLog.info(String.format("Reason : ",te.toString()));
                    }
                }
            }
            catch(InterruptedException ie){
                MyLog.info(String.format("Something unexpedted happenned, %s",ie.toString()));
            }
        }
    }
    
}
