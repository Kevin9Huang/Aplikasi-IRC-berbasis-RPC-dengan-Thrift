/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Server;

import MessageServiceThrift.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.log4j.BasicConfigurator;
/**
 *
 * @author Kevin
 */
public class Server {
    private static final Logger MyLog = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) throws Exception{
        BasicConfigurator.configure();
        
        final MessageDistributor messageDistributor = new MessageDistributor();
        new Thread(messageDistributor).start();
        TProcessorFactory processFactory = new TProcessorFactory(null){
            @Override
            public TProcessor getProcessor(TTransport trans){
                messageDistributor.addClient(new MessageServiceClient(trans,MessageDistributor.MaxClientNumber));
                return  new MessageService.Processor(messageDistributor);
            }
        };
        Scanner sc = new Scanner(System.in);
        System.out.print("Input port: ");
        int port = sc.nextInt();
        TServerTransport serverTransport = new TServerSocket(port);
        TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
        serverArgs.processorFactory(processFactory);
        TServer server = new TThreadPoolServer(serverArgs);
        MyLog.info("Server started");
        server.serve();
    }
    
}
