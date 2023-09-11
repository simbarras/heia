package ch.epfl.general_libraries.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketDisplayer {
	
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(48000);		
			ss.setReuseAddress(true);
			while (true) {
				try {
					final Socket socket = ss.accept();
					System.out.println("acepted");
					InputStream is = socket.getInputStream();
				    byte[] buf=new byte[1024];
				    int count=-1;		
					while(((count=is.read(buf))>0)) {
						System.out.write(buf,0,count);
					}
					is.close();		
				}
				catch (Exception e) {
				}
				finally {
					try {
						ss.close();	
					}
					catch(IOException e) {
					}
				}	
			}						
		}
		catch (IOException f) {
		}		
	}
	
	
}
