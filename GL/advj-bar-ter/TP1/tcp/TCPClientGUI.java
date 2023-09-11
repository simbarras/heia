/**
 * File    : TCPClientGUI.java
 * Author  : R. Scheurer (HEIA-FR)
 * Version : v1.23 / 17.02.2023
 *
 * Very simple GUI for a simple TCP client
 *
 * DO NOT MODIFY THIS FILE ! (ask prof if you REALLY think you need to)
 *
 */
package sockets.tcp;

import java.awt.*;
import java.awt.event.*;
import java.net.*;

@SuppressWarnings("serial")
public class TCPClientGUI extends Frame implements ActionListener {

  final static String VERSION      = "v1.23";
  final static int    SERVERPORT   = 7878;
  final static String DEFAULT_HOST = "127.0.0.1";
  final static String CONNECT      = "Connect";
  final static String DISCONNECT   = "Disconnect";
  final static String SEND         = "Send";

  InetSocketAddress   isa;
  TCPClient           client;
  TextField           hostField;
  Label               hostLabel;
  TextField           portField;
  Label               portLabel;
  Button              hostButton;
  TextField           msgField;
  Label               msgLabel;
  Button              sndButton;
  Label               statusLabel;

  // -----------------------------------------------------------------
  // Constructor (includes GUI setup)
  // -----------------------------------------------------------------
  public TCPClientGUI() {
    this.setLayout(null);

    // Host information
    hostField = new TextField(15);
    hostField.setText(DEFAULT_HOST);
    add(hostField);
    hostField.setBounds(80, 50, 200, 30);

    hostLabel = new Label("Host :");
    add(hostLabel);
    hostLabel.setBounds(10, 50, 40, 30);

    // Port information
    portLabel = new Label("Port :");
    add(portLabel);
    portLabel.setBounds(300, 50, 40, 30);

    portField = new TextField(5);
    portField.setText("" + SERVERPORT);
    add(portField);
    portField.setBounds(340, 50, 50, 30);

    hostButton = new Button(CONNECT);
    add(hostButton);
    hostButton.addActionListener(this);
    hostButton.setBounds(400, 50, 80, 30);

    // Message information
    msgField = new TextField(30);
    add(msgField);
    msgField.setBounds(80, 100, 310, 30);
    msgField.setEditable(false);
    msgField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
          actionPerformed(new ActionEvent(this,0,SEND));
        }
      }
    });

    msgLabel = new Label("Message :");
    add(msgLabel);
    msgLabel.setBounds(10, 100, 90, 30);

    sndButton = new Button(SEND);
    add(sndButton);
    sndButton.addActionListener(this);
    sndButton.setBounds(400, 100, 80, 30);
    sndButton.setEnabled(false);

    // Status line
    statusLabel = new Label(
            "  Please enter destination of TCP connection (host/port) ...");
    add(statusLabel);
    statusLabel.setBounds(0, 150, 500, 30);
    statusLabel.setBackground(Color.lightGray);

    setTitle("Sockets Lab - TCPClientGUI (" + VERSION + ")");

    // call exitGUI() when closing frame
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        exitGUI();
      }
    });
  }

  // -----------------------------------------------------------------
  // Handling GUI actions
  // -----------------------------------------------------------------
  public void actionPerformed(ActionEvent ae) {

    String cmd = ae.getActionCommand();
    setStatus("...");

    // --- command CONNECT -------------------------------------------
    if (cmd.equals(CONNECT)) { // Set Command
      setStatus("Connecting ...");
      try {
        InetAddress ia = InetAddress.getByName(hostField.getText());
        int port = Integer.parseInt(portField.getText());
        isa = new InetSocketAddress(ia, port);
        client = new TCPClient(isa);
        setGUIconnected(true);
        setStatus("Socket creation OK.");
      } catch (UnknownHostException e) {
        setStatus("ERROR: Invalid host address!");
      } catch (NumberFormatException e) {
        setStatus("ERROR: Invalid port number!");
      } catch (SocketException e) {
        setStatus("ERROR: Socket creation failed: " + e);
        System.out.println(e);
        // e.printStackTrace();
      } catch (Exception e) {
        setStatus("ERROR: " + e);
      }
    }
    // --- command DISCONNECT ----------------------------------------
    else if (cmd.equals(DISCONNECT)) {
      try {
        setGUIconnected(false);
        client.closeSocket();
        // hostField.setText("");
        msgField.setText("");
        setStatus("Socket closed.");
      } catch (Exception e) {
        e.printStackTrace();
        setStatus("Closing of socket FAILED!");
      }
    }
    // --- command SEND ----------------------------------------------
    else if (cmd.equals(SEND)) {
      try {
        String txt = msgField.getText();
        client.sendText(txt);
        msgField.setText("");
        setStatus("Message sent.");
      } catch (Exception e) {
        System.out.println(e);
        setStatus("Error sending message! Closing socket ...");
        try {
          client.closeSocket();
          setStatus("Error sending message! Closing socket ... Done");
        } catch (Exception ei) {
          e.printStackTrace();
          setStatus("Error sending message AND error on closing socket ... !");
        }
        setGUIconnected(false);
      }
    }
    // --- command ?? ------------------------------------------------
    else {
      setStatus("WARNING: unknown command.");
    }
  }

  // -----------------------------------------------------------------
  // Internal utility methods
  // -----------------------------------------------------------------
  void exitGUI() {
    try {
      if (client != null)
        client.closeSocket();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }

  void setStatus(String txt) {
    statusLabel.setText("  " + txt);
  }

  void setGUIconnected(boolean connected) {
    hostButton.setLabel(connected ? DISCONNECT : CONNECT);
    hostField.setEditable(!connected);
    portField.setEditable(!connected);
    sndButton.setEnabled(connected);
    msgField.setEditable(connected);
    msgField.setText("");
  }

  // -----------------------------------------------------------------
  // MAIN (22/23)
  // -----------------------------------------------------------------
  public static void main(String[] args) {
    TCPClientGUI gui = new TCPClientGUI();
    gui.setFont(new Font("Arial", 0, 12));
    gui.setSize(500, 180);
    gui.setResizable(false);
    gui.setVisible(true);
    gui.repaint();
  }
}
