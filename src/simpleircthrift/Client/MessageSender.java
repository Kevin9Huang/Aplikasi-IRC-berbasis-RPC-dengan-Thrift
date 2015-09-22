/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import MessageServiceThrift.Message;
import MessageServiceThrift.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import simpleircthrift.Server.Server;

/**
 *
 * @author Kevin
 */
public class MessageSender extends ConnectionRequiredRunnable{
    private static final Logger MyLog = LoggerFactory.getLogger(MessageSender.class);
    private final MessageService.Client client;
    private final BlockingQueue<Message> msgSendQueue;
    public MessageSender(
            TProtocol protocol,
            ConnectionStatusMonitor connectionStatusMonitor){
        super(connectionStatusMonitor,"Message Sender");
        this.client = new MessageService.Client(protocol);
        this.msgSendQueue = new LinkedBlockingQueue<Message>();
    }
    
    public void send(Message msg){
        msgSendQueue.add(msg);
    }
    
    public int RequestID() throws TException{
        return client.RequestID();
    }
    
    public void run(){
        connectWait();
        while(true){
            try{
                if(!msgSendQueue.isEmpty()){
                    
       
                    Message msg = msgSendQueue.take();
                    if(msg.message != "" && msg != null){
                        try{
                            client.sendMessage(msg);
                        }
                        catch(TException e){
                            msgSendQueue.add(msg);
                            disconnected();
                        }
                    }
                }
            }
            catch(InterruptedException ie){
                connectWait();
            }
        }
    }
}
