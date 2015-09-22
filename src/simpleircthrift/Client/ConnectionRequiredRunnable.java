/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import com.sun.istack.internal.logging.Logger;

/**
 *
 * @author Kevin
 */
public class ConnectionRequiredRunnable implements ConnectionStatusListener,Runnable{
    private static final Logger MyLog = Logger.getLogger(ConnectionRequiredRunnable.class);
    protected final ConnectionStatusMonitor connectionMonitor;
    protected  final String threadName;
    protected Thread executingThread;
    
    public ConnectionRequiredRunnable(ConnectionStatusMonitor connectionStatusMonitor,String name){
        this.connectionMonitor = connectionStatusMonitor;
        this.connectionMonitor.addListener((ConnectionStatusListener) this);
        this.threadName = name;
    }
    
    protected void disconnected(){
        MyLog.info(String.format("%s diconnected from server",threadName));
        connectionMonitor.disconnected((ConnectionStatusListener) this);
        connectWait();
    }
    
    protected synchronized void connectWait(){
        executingThread = Thread.currentThread();
        try{
            MyLog.info(String.format("%s waiting for connection",threadName));
            wait();
        }
        catch(InterruptedException e){
            MyLog.info(String.format("%s caught InterruptedException",threadName));
        }
        MyLog.info(String.format("%s connected, resume execution",threadName));
    }
    
    public synchronized void connectionLost(){
        executingThread.interrupt();
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void connectionEstabilished() {
        notifyAll();
    }
}
