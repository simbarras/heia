package ch.epfl.general_libraries.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class TransparentProxy {
	
	private static int localPort = -1;
	private static int redirPort = -1;
	private static int killPort = -1;
	private static String redirURL = null;
	
	private static int maxThread = 20;
	private static int currentThread = 0;
	
	private static Logger logger = org.apache.log4j.Logger.getLogger(TransparentProxy.class);
	
	
	public static void main(String[] args) throws Exception {
		if(args.length > 0) {
			if (args[0].equals("proxy")) {
				for (int i = 1 ; i <= args.length - 1 ; i = i+2) {
					if (args[i].equals("-local")) {
						localPort = Integer.parseInt(args[i+1]);
					} else if (args[i].equals("-redirIP")) {
						redirURL = args[i+1];
					} else if (args[i].equals("-redirport")) {
						redirPort = Integer.parseInt(args[i+1]);
					} else if (args[i].equals("-killport")) {
						killPort = Integer.parseInt(args[i+1]);
					}
				}
				if (localPort < 0 || redirPort < 0 || killPort < 0 || redirURL == null) {
					System.out.println("Wrong usage : use -local [port] -redirIP [IP] -redirport [port] -killport [port]");
					System.exit(0);
				}
				startKillerThread();
				startThread();				
			} else if (args[0].equals("killproxy")) {
				int port = Integer.parseInt(args[1]);
				killProxy(port);
			} else {
				System.out.println("Wrong usage : either proxy, either killproxy");
				System.exit(0);
			}
		}
	}
	
	private static Object sem = new Object();
	
	private static void killProxy(int i) throws Exception {
		Socket s = new Socket("localhost", i);
		OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
		osw.write("KILL ME\n");
		osw.flush();
	}
	
	private static void startKillerThread() {
		Thread t = new Thread() {
			public void run() {
				try {
					while (kill());
				} catch (Exception e) {
				}
			}
		};
		t.start();
	}
	
	private static boolean kill() throws Exception {
		Thread.currentThread().setName("ProxyKiller");
		ServerSocket ss = new ServerSocket(killPort);
		ss.setReuseAddress(true);
		Socket socket = ss.accept();
		BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String read = bis.readLine();
		logger.info("KILL Tentative");
		if (read.equals("KILL ME")) {		
			try {
				ss.close();
			}
			catch (Exception e) {}
			System.exit(0);	
		} else {
			logger.info("KILL String : " + read);
			ss.close();
			return true;
		}	
		return false;
	}
	
	private static void startThread() throws Exception {
		ServerSocket ss = new ServerSocket(localPort);		
		ss.setReuseAddress(true);
		while (true) {
			try {
				final Socket socket = ss.accept();
				logger.info(">>> Incoming connection : " + socket.getInetAddress());
				if (currentThread < maxThread*2) {
					final Socket remote = new Socket(redirURL, redirPort);
					logger.info("++  Connection accepted");
					Thread t = new Thread() {
						public void run() {
							try {
								serve(socket, remote);
							}
							catch (Exception e) {
							}
						}
					};
					t.start();		
					t = new Thread() {
						public void run() {
							try {
								serve(remote, socket);
							}
							catch (Exception e) {
							}
						}
					};
					t.start();
				} else {
					logger.info("--  Connection refused");
					socket.close();
				}
			}
			catch (Exception e) {
				logger.info("Failed somewhere", e);
			}							
		}
	}
	
	private static void serve(Socket s1, Socket s2) {
		synchronized (sem) {
			currentThread++;
			Thread.currentThread().setName("T_" + currentThread);
		}
		logger.debug(Thread.currentThread().getName() + " started");
		try {
			InputStream is = s1.getInputStream();
			OutputStream os = s2.getOutputStream();
	
		    byte[] buf=new byte[1024];
		    int count=-1;		
			while(((count=is.read(buf))>0)) {
				os.write(buf,0,count);
			}
			os.close();
			is.close();
			s1.close();
			s2.close();
			logger.debug(Thread.currentThread().getName() + " finished correctly");
		}
		catch (Exception e) {
			logger.debug(Thread.currentThread().getName() + " finished uncorrectly");			
		}
		finally {
			synchronized (sem) {
				currentThread--;
			}
		}
	}
}
