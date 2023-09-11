/**
 * File   : TCPClient.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 17.02.2023
 * <p>
 * Simple TCP client
 */
package sockets.tcp;

import java.net.*;
import java.io.*;

public class TCPClient {

    private Socket s;
    private ObjectOutputStream oos;
    private int counter;

    public TCPClient(InetSocketAddress isa) throws IOException {
        s = new Socket(isa.getAddress(), isa.getPort());
        oos = new ObjectOutputStream(s.getOutputStream());
        counter = 0;
    }

    public void closeSocket() throws IOException {
        if (oos != null) {
            oos.close();
            oos = null;
        }
        if (s != null) {
            s.close();
            s = null;
        }
    }

    public void sendText(String txt) throws IOException {
        if (oos == null) {
            throw new IOException("Socket is closed");
        }
        MyMessage m = new MyMessage(counter++, txt);
        System.out.println("Send msg " + m.getCounter() + ": " + m.getMessage() + " to server");
        oos.writeObject(m);
        oos.flush();
        System.out.println("Msg " + m.getCounter() + " sent to server");
    }
}
