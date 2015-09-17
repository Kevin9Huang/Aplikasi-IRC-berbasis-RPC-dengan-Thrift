/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import com.sun.istack.internal.logging.Logger;

/**
 *
 * @author Kevin
 */
public class ConnectionStatusMonitor {
    private static final Logger MyLog = Logger.getLogger(ConnectionStatusMonitor.class);
    private class RetryTask extends TimerTask{

        @Override
        public void run() {
            tryOpen();
        }
        
    }
    private final Timer timer;
    private final TTransport transport;
    private final AtomicBoolean connected;
    private final List<ConnectionStatusListener> listeners;
    public ConnectionStatusMonitor(TTransport transport){
        this.transport = transport;
        this.connected = new AtomicBoolean(false);
        this.listeners = new ArrayList<ConnectionStatusListener>();
        this.timer = new Timer();
    }
    public void addListener(ConnectionStatusListener listener){
        listeners.add(listener);
    }
    
    public void disconnected(ConnectionStatusListener noticer){
        if(connected.compareAndSet(true,false)){
            for(ConnectionStatusListener listener : listeners){
                if(listener == noticer) continue;
                    listener.connectionLost();
            }
            timer.schedule(new RetryTask(),5*1000);
        }
    }
    
    public void tryOpen(){
        if(connected.get())return;
        transport.close();
        try{
            transport.open();
            connected.set(true);
            for(ConnectionStatusListener listener : listeners){
                listener.connectionEstabilished();
            }
            return;
        }
        catch(TTransportException e){
            MyLog.info(String.format("Something happenned : %s", e));
        }
        timer.schedule(new RetryTask(),5*1000);
    }
}
