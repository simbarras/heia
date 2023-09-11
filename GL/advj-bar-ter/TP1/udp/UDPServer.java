/**
 * File   : UDPServer.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 17.02.2023
 * <p>
 * Simple UDP server template
 */
package sockets.udp;

import sockets.tcp.TCPServer;

import java.io.IOException;
import java.net.*;


public class UDPServer {

    static final int MAX_SIZE = 100;
    static final int SERVER_PORT = 8787;  // port to use

    private static int counter = 0;


    public static void main(String[] args) throws IOException {
        byte[] buffer = new byte[MAX_SIZE];
        DatagramSocket ds = new DatagramSocket(SERVER_PORT);
        DatagramPacket dp = new DatagramPacket(buffer, MAX_SIZE);

        while (true) {
            ds.receive(dp);
            counter++;
            String msg = new String(dp.getData(), 0, dp.getLength());
            TCPServer.print("Message " + counter + " from " + dp.getAddress() +" --> "+dp.getPort()+" send " + msg);
        }
    }
}
