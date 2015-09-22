/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import MessageServiceThrift.Message;
import MessageServiceThrift.MessageService;
import com.sun.istack.internal.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 *
 * @author Kevin
 */
public class Client implements  MessageService.Iface{
    private static final Logger MyLog = Logger.getLogger(Client.class);
    private final ConnectionStatusMonitor connectionMonitor;
    private final MessageSender sender;
    private final MessageReceiver receiver;
    private int clientid;
    private final TTransport transport;
    private final TProtocol protocol;
    private final List<MessageListener> listeners;
    
    public Client(int clientid,String server,int port,MessageService.Iface messageHandler){
        this.clientid = -99;
        this.transport = new TSocket(server,port);
        this.protocol = new TBinaryProtocol(transport);
        this.connectionMonitor = new ConnectionStatusMonitor(transport);
        this.sender = new MessageSender(protocol, connectionMonitor);
        this.receiver = new MessageReceiver(protocol, messageHandler, connectionMonitor);
        new Thread(sender).start();
        new Thread(receiver).start();
        this.connectionMonitor.tryOpen();
        this.listeners = new ArrayList<MessageListener>();
    }
    
    public void addListener(MessageListener listener){
        listeners.add(listener);
    }
    public void sendMessageToServer(String msg){
        sender.send(new Message(clientid,msg));
    }
    @Override
    public void sendMessage(Message msg) throws TException {
        for(MessageListener listener : listeners){
           listener.messageReceived(msg);
        }
    }
    
    @Override
    public int RequestID() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) throws Exception{
        MessageService.Iface handler = new MessageService.Iface() {
            @Override
            public void sendMessage(Message msg) throws TException {
                System.out.println(msg.message);
            }

            
            @Override
            public int RequestID() throws TException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        Scanner sc = new Scanner(System.in);
        System.out.print("Input port: ");
        int port = sc.nextInt();
        Client client = new Client(-99,"localhost",port,handler);
        TTransport transport = new TSocket("localhost",port);
        transport.open();
        MessageService.Client requestid = new MessageService.Client(new TBinaryProtocol(transport));
        client.clientid = requestid.RequestID();
        String userinput = "";
        while(!userinput.equals("/EXIT")){
            System.out.print("Input message: ");
            userinput = sc.nextLine();
            if(userinput != ""){
              client.sendMessageToServer(userinput);
              Thread.sleep(1000);
            }
        }
        //transport.close();
        System.exit(0);
    }
    
}
