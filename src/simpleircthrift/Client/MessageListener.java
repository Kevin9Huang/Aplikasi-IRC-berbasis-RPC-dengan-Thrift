/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

import MessageServiceThrift.Message;

/**
 *
 * @author Kevin
 */
public interface MessageListener {
    public void messageReceived(Message msg);
}
