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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.logging.Level;
import org.apache.thrift.TException;

/**
 *
 * @author Kevin
 */
public class MessageDistributor implements Iface,Runnable {

    private final static Logger MyLog = LoggerFactory.getLogger(MessageDistributor.class);
    private BlockingQueue<Message> messageQueue;
    private List<MessageServiceClient> clients;
    public static int MaxClientNumber = 0;
    public List<String> AllChannel = new ArrayList<String>();
    
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
                if(msg.message != "" || msg.message != null){
                    String firstWord = "";
                    String MessageContains = "";
                    boolean foundclient = false;
                    MessageServiceClient clientFound = null;
                    Iterator<MessageServiceClient> clientItr = clients.iterator();
                    while(clientItr.hasNext() && !foundclient){
                        clientFound = clientItr.next();
                        if(clientFound != null){
                            if(msg.clientnid == clientFound.getID()){
                                foundclient = true;
                            }
                        }
                    }
                    if(msg.message.contains(" ")){
                       firstWord= msg.message.substring(0,msg.message.indexOf(" "));
                       MessageContains = msg.message.substring(msg.message.indexOf(" ")+1,msg.message.length());
                    }
                    if(foundclient){
                        switch(firstWord){
                            case "/NICK" : 
                                clientFound.setNick(MessageContains);
                                break;
                            case "/JOIN" : 
                                if(!AllChannel.contains(MessageContains)){
                                    AllChannel.add(MessageContains);
                                }
                                if(!clientFound.getActiveChannel().contains(MessageContains)){
                                    clientFound.addChannel(MessageContains);
                                }
                                break;
                            case "/LEAVE" :
                                if(clientFound.getActiveChannel().contains(MessageContains)){
                                    clientFound.removeChannel(MessageContains);
                                }
                                break;
                            case "/EXIT" : 
                                clients.remove(clientFound);
                            default : //Send Message to All User Channel and to specific channel here
                                try {
                                    if(msg.message != null && msg.message.length() > 0){
                                        if(msg.message.charAt(0) == '@' && msg.message.contains(" ")){
                                            String SendToChannel = msg.message.substring(1,msg.message.indexOf(" "));
                                            MessageServiceClient ClientTemp;
                                            Iterator<MessageServiceClient> clientItr2 = clients.iterator();
                                            while(clientItr2.hasNext()){
                                                ClientTemp = clientItr2.next();
                                                if(ClientTemp.getActiveChannel().contains(SendToChannel)){
                                                    ClientTemp.sendMessage(new Message(msg.clientnid ,"["+SendToChannel+"]("+clientFound.getNick()+")"+msg.getMessage().substring(msg.getMessage().indexOf(" ")+1,msg.getMessage().length())));
                                                }
                                            }
                                        }
                                        else{
                                            List<String> ChannelList = clientFound.getActiveChannel();
                                            Iterator<String> itrChannel = ChannelList.iterator();
                                            while(itrChannel.hasNext()){
                                                String SendToChannel = itrChannel.next();
                                                MessageServiceClient ClientTemp;
                                                Iterator<MessageServiceClient> clientItr2 = clients.iterator();
                                                while(clientItr2.hasNext()){
                                                    ClientTemp = clientItr2.next();
                                                    if(ClientTemp != null){
                                                        if(ClientTemp.getActiveChannel().contains(SendToChannel)){
                                                            ClientTemp.sendMessage(new Message(msg.clientnid ,"["+SendToChannel+"]("+clientFound.getNick()+")"+msg.getMessage()));
                                                        }
                                                    }
                                                    
                                                }
                                            }
                                        }
                                    }
                                    
                                    //clientFound.sendMessage(new Message(-99 , msg.getMessage()+" from default"));
                                } catch (TException te) {
                                    clients.remove(clientFound);
                                    MyLog.info(String.format("Removing %s from client list",clientFound.getAddy()));
                                    MyLog.info(String.format("Reason : ",te));
                                }

                        }
                    }
                }
            }
            catch(InterruptedException ie){
                MyLog.info(String.format("Something unexpedted happenned, %s",ie.toString()));
            }
        }
    }

    @Override
    public int RequestID() throws TException {
        return MaxClientNumber++;
    }
    
}
