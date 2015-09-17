/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import MessageServiceThrift.MessageService;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;

/**
 *
 * @author Kevin
 */
public class MessageReceiver extends ConnectionRequiredRunnable{
    private final MessageService.Processor processor;
    private final TProtocol protocol;
    
    public MessageReceiver(
        TProtocol protocol,
        MessageService.Iface messageService,
        ConnectionStatusMonitor connectionStatusMonitor)
        {
            super(connectionStatusMonitor,"Message Receiver");
            this.protocol= protocol;
            this.processor = new MessageService.Processor(messageService);
        }
    @Override
    public void run(){
        connectWait();
        while(true){
            try{
                while(processor.process(protocol, protocol) == true){}
            }
            catch(TException e){
                disconnected();
            }
        }
    }
}

