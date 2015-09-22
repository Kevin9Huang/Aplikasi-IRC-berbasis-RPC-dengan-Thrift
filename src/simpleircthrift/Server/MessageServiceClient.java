/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Server;

import MessageServiceThrift.Message;
import MessageServiceThrift.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 *
 * @author Kevin
 */
public class MessageServiceClient {
    private final TTransport transport;
    private final String addy;
    private final int port;
    private final int clientID;
    private String nickName;
    private final MessageService.Client client;
    private List<String> ChannelsJoined;
    
    public MessageServiceClient(TTransport transport,int clientID){
        TSocket tSocket = (TSocket)transport;
        this.transport = transport;
        this.client = new MessageService.Client(new TBinaryProtocol(transport));
        this.addy = tSocket.getSocket().getInetAddress().getHostAddress();
        this.port = tSocket.getSocket().getPort(); 
        this.clientID = clientID;
        this.ChannelsJoined = new ArrayList<String>() {};
        this.nickName = randomName();
    }
    
    public String getAddy(){
        return addy;
    }
    
    public int getID(){
        return clientID;
    }
    
    public void removeChannel(String ChannelName){
        if(ChannelsJoined.contains(ChannelName)){
            ChannelsJoined.remove(ChannelName);
        }
    }
    
    public void addChannel(String ChannelName){
        if(!ChannelsJoined.contains(ChannelName)){
            ChannelsJoined.add(ChannelName);
        }
    }
    
    public List<String> getActiveChannel(){
        return ChannelsJoined;
    }
    
    public void sendMessage(Message msg) throws TException{
        this.client.sendMessage(msg);
    }
    
    public String randomName(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    public void setNick(String NickName){
        this.nickName = NickName;
    }
    
    public String getNick(){
        return nickName;
    }
}
