/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Server;

import MessageServiceThrift.Message;
import MessageServiceThrift.MessageService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 *
 * @author Kevin
 */
public class MessageServiceClient {
    protected final TTransport transport;
    protected final String addy;
    protected final int port;
    protected final MessageService.Client client;
    
    public MessageServiceClient(TTransport transport){
        TSocket tSocket = (TSocket)transport;
        this.transport = transport;
        
        this.client = new MessageService.Client(new TBinaryProtocol(transport));
        this.addy = tSocket.getSocket().getInetAddress().getHostAddress();
        this.port = tSocket.getSocket().getPort();            
    }
    
    public String getAddy(){
        return addy;
    }
    
    public void sendMessage(Message msg) throws TException{
        this.client.sendMessage(msg);
    }
}
