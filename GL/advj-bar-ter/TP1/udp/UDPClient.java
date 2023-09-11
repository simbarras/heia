/**
 * File   : UDPClient.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 17.02.2023
 * 
 * Simple UDP client
 * 
 */
package sockets.udp;

import java.net.*;

public class UDPClient {
 
  DatagramSocket ds;
  DatagramPacket dp;
  byte[] buffer;

  public UDPClient(){
    System.out.println("New udp client");
    try {
      ds = new DatagramSocket();
//      buffer = new byte[UDPClientGUI.MAXSIZE];
//      dp = new DatagramPacket(buffer, UDPClientGUI.MAXSIZE);
    } catch (SocketException e) {
      e.printStackTrace();
    }

  }

  public void closeSocket() {
    System.out.println("Close socket");
    ds.close();
  }

  public String getPort() {
    System.out.println("Get port");
    return "" + ds.getLocalPort();
  }

  public void sendMessage(InetSocketAddress isaDest, String msg) {
    try {
      buffer = msg.getBytes("UTF-8");
      dp = new DatagramPacket(buffer, buffer.length, isaDest);
      ds.send(dp);
      System.out.println("Message with length: " +buffer.length + " sent to: "+isaDest);
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
