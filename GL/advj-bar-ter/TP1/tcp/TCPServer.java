/**
 * File   : TCPServer.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 17.02.2023
 * <p>
 * Simple TCP server template
 */
package sockets.tcp;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    static final int SERVER_PORT = 7878;  // server port to use
    static int id = 0;                    // client id to start with

    private static ServerSocket serverSocket;
    private static ExecutorService pool;


    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        pool = Executors.newFixedThreadPool(10);

        try {
            while (true) {
                pool.execute(new TCPClientHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            pool.shutdown();
            e.printStackTrace();
        }
    }

    public static void print(String msg) {
        System.out.println(msg);
    }

    static class TCPClientHandler implements Runnable {
        Socket s;
        ObjectInputStream ois;
        int id;

        public TCPClientHandler(Socket s) throws IOException {
            this.s = s;
            id = TCPServer.id++;
            ois = new ObjectInputStream(s.getInputStream());
            TCPServer.print("#" + id + " status : connected " + s.getInetAddress() + ":" + s.getPort());
        }

        @Override
        public void run() {
            try {
                // Show message received from the client
                while (true) {
                    MyMessage m = (MyMessage) ois.readObject();
                    TCPServer.print("#" + id + " message " + m.getCounter() + ": " + m.getMessage());
                }
            } catch (EOFException e) {
                TCPServer.print("#" + id + " status : client left");
                try {
                    ois.close();
                    ois = null;
                    s.close();
                    s = null;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
