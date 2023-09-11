package ch.epfl.javancox.execdeamon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class ClassServer {
	
	private boolean aboutToDie = false;
	private ClassLoader ld;
	String localIP = "128.59.65.177";	

	public ClassServer() {
		ld = this.getClass().getClassLoader();
	}
	
	public void start() throws IOException {
		ServerSocket s = new ServerSocket(21210);
		startThread(s);	
	}
	
	public String getAddress() {
		return localIP;
	}
	
	public void stop() {
		aboutToDie = true;
		try {
			Socket s = new Socket("localhost",21210);			
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startThread(final ServerSocket ss) {
		Thread t = new Thread() {
			public void run() {
				while(true) {
					Socket s;
					try {
						s = ss.accept();
						if (aboutToDie == false) {
							attendConnection(s);
						} else {
							return;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};
		t.setName("ClassServer listening thread");
		t.start();		
	}
	
	/*
	 * This is not the most secure way to do, if connecting clients do not follow the protocol,
	 * thread will never terminate and might because a zombie
	 */
	private void attendConnection(final Socket s) throws IOException {
		Thread t = new Thread() {
			public void run() {
				try {
					ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
					int b = 0;
					while ((b = s.getInputStream().read()) != 10) {
						byteArray.write(b);
					}
					String[] typeName = new String(byteArray.toByteArray()).split("@");
					String type = typeName[0];
					String name = typeName[1];
					if (type.equals("class")) {
						attendConnectionClass(s, name);
					} else {
						attendConnectionProperties(s, name);
					}
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		};
		t.start();
	}
	
	private void attendConnectionClass(Socket s, String name) throws IOException {		
		InputStream in;
		Class c = null;
		try {
			// trying to get it as a class
			try {
				c = Class.forName(name);
			}
			catch(ClassNotFoundException e) {
				int index = name.lastIndexOf(".");
				String newName = name.substring(0, index);
				newName = newName + "$" + name.substring(index+1, name.length());
				c = Class.forName(newName);
			}
			
			in = ld.getResourceAsStream(c.getName().replace('.', '/') + ".class");

			byte[] buffer = new byte[4096]; // Adjust if you want
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				s.getOutputStream().write(buffer, 0, bytesRead);
			}
		} catch (ClassNotFoundException e) {
			s.getOutputStream().write(new byte[]{0,0,0,0});
		}
		s.close();
	}
	
	private void attendConnectionProperties(Socket s, String name) throws IOException {
		URL r = ld.getResource(name);
		if (r != null) {
			InputStream in = ld.getResourceAsStream(name);
			byte[] buffer = new byte[4096]; // Adjust if you want
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				s.getOutputStream().write(buffer, 0, bytesRead);
			}					
		} else {
			s.getOutputStream().write(new byte[]{0,0,0,0});			
		}
		s.close();
	}
}

