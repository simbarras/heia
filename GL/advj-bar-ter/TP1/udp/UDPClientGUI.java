/**
 * File    : UDPClientGUI.java
 * Author  : R. Scheurer (HEIA-FR)
 * Version : v1.23 / 17.02.2023
 * 
 * Very simple GUI for a simple UDP client
 * 
 * DO NOT MODIFY THIS FILE !
 */
package sockets.udp;

import java.net.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class UDPClientGUI extends Frame implements ActionListener {

  final static String VERSION           = "v1.23";
  final static int    SERVERPORT        = 8787;
  final static String DEFAULT_HOST      = "127.0.0.1";
  final static String LOCK              = "Lock";
  final static String UNLOCK            = "Unlock";
  final static String SEND              = "Send";
  static final int    MAXSIZE           = 50;

  InetSocketAddress   isaDest;
  UDPClient           client;
  TextField           hostField;
  Label               hostLabel;
  Button              lockButton;
  TextField           portField;
  Label               portLabel;
  TextField           msgField;
  Button              sndButton;
  Label               msgLabel;
  Label               statusLabel;
  boolean             destinationLocked = true;

  public UDPClientGUI() {
    this.setLayout(null);

    // Host information
    hostField = new TextField(20);
    hostField.setText(DEFAULT_HOST);
    add(hostField);
    hostField.setBounds(80, 50, 200, 30);
    hostField.setEditable(false);

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
    portField.setEditable(false);

    // Lock/Unlock button
    lockButton = new Button(UNLOCK);
    add(lockButton);
    lockButton.addActionListener(this);
    lockButton.setBounds(420, 50, 40, 30);

    // Message information
    msgField = new TextField(30);
    add(msgField);
    msgField.setBounds(80, 100, 310, 30);
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

    // Send button
    sndButton = new Button(SEND);
    add(sndButton);
    sndButton.addActionListener(this);
    sndButton.setBounds(420, 100, 40, 30);

    // Status line
    statusLabel = new Label(
	"  Please enter destination for UDP datagrams and click SET ...");
    add(statusLabel);
		statusLabel.setBounds(0, 150, 500, 30);
    statusLabel.setBackground(Color.lightGray);

    setTitle("Sockets Lab - UDPClientGUI (" + VERSION + ")");

    // exit when closing frame
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        ((UDPClientGUI) evt.getSource()).exit();
      }
    });

    // create client socket
    try {
      client = new UDPClient();
//    } catch (SocketException e) {
//      System.out.println("ERROR: UDP socket creation failed!");
//      System.exit(1);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
      System.exit(1);
    }
	setStatus("UDP socket on port " + client.getPort() + " successfully created.");
    setTitle("Sockets Lab - UDPClientGUI (port " + client.getPort() + ")");
    isaDest = getDestination(); // starting in locked state

  }

  void exit() {
    if (client != null)
      client.closeSocket();
    System.exit(0);
  }

  // retrieve destination address and port from GUI
  private InetSocketAddress getDestination() {
    try {
      InetAddress ia = InetAddress.getByName(hostField.getText());
      int port = Integer.parseInt(portField.getText());
      return new InetSocketAddress(ia, port);
    } catch (UnknownHostException e) {
      setStatus("ERROR: Invalid host address!");
    } catch (NumberFormatException e) {
      setStatus("ERROR: Invalid port number!");
    } catch (Exception e) {
      setStatus("ERROR: " + e);
    }
    return null;
  }

  public void actionPerformed(ActionEvent ae) {

    String cmd = ae.getActionCommand();
    setStatus("...");

	if (cmd.equals(LOCK)) { // Lock button
      isaDest = getDestination();
      if (isaDest != null) {
        lockButton.setLabel(UNLOCK);
        hostField.setEditable(false);
        portField.setEditable(false);
        destinationLocked = true;
        setStatus("Destination locked.");
      }
    } else if (cmd.equals(UNLOCK)) { // Unlock button
      lockButton.setLabel(LOCK);
      hostField.setEditable(true);
      portField.setEditable(true);
      destinationLocked = false;
      setStatus("Destination unlocked.");
    } else if (cmd.equals(SEND)) { // Send button
      try {
        if (!destinationLocked)
          isaDest = getDestination();
        if (isaDest == null)
          return;
        String msg = msgField.getText();
        if (msg.length() <= MAXSIZE) {
          setStatus("Sending message ...");
          client.sendMessage(isaDest, msg);
          msgField.setText("");
          setStatus("Message sent.");
        } else {
					setStatus("ERROR: Message too long (>50 chars), not sent!");         
        }
      } catch (Exception e) {
        System.out.println(e);
        setStatus("ERROR: Message sending failed!");
      }
    } else {
      setStatus("WARNING: unknown command.");
    }

  }

  // display info on status line
  void setStatus(String msg) {
    statusLabel.setText("  " + msg);
  }

  // MAIN (22/23)
  public static void main(String[] args) {
    UDPClientGUI gui = new UDPClientGUI();
    gui.setFont(new Font("Arial", 0, 12));
    gui.setSize(500, 180);
    gui.setResizable(false);
    gui.setVisible(true);
    gui.repaint();
  }

}
