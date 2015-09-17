/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleircthrift.Client;

/**
 *
 * @author Kevin
 */
public interface ConnectionStatusListener {
    public void connectionLost();
    public void connectionEstabilished();
}
