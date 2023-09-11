package ch.epfl.javancox.execdeamon;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;






import ch.epfl.general_libraries.simulation.Time;

public class DeamonClient {

	public static void main(String[] args) throws IOException {
		Time t = new Time(10);
		Object[] array = new Object[]{t,t};
		ClassServer classServer = new ClassServer();
		classServer.start();
		DeamonClient d = new DeamonClient(classServer.getAddress(), "128.59.65.161");	
		d.send(array);
	}	

	public DeamonClient(String classServerAddress, String remoteAddress) throws IOException {
		this.classServerIPAsbyte = classServerAddress.getBytes();
		this.remoteIP = remoteAddress;
		initConnection();
	}

	public void close() throws IOException {
		oos.close();
		ois.close();
		connectionWithServerToSendTaskAndRetrieveData.close();
	}

	Socket connectionWithServerToSendTaskAndRetrieveData;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	byte[] classServerIPAsbyte;
	String remoteIP;	

	private boolean initConnection() throws IOException {	
		try {
			connectionWithServerToSendTaskAndRetrieveData = new Socket(remoteIP, 22111);
			OutputStream outputStream = connectionWithServerToSendTaskAndRetrieveData.getOutputStream();
			outputStream.write(classServerIPAsbyte);
			outputStream.write(10);
			oos = new ObjectOutputStream(outputStream);
			ois = new ObjectInputStream(connectionWithServerToSendTaskAndRetrieveData.getInputStream());
			return true;
		}
		catch (IOException e) {
			return false;
		}	
	}

	public Object send(Object o) throws IOException {
		try {
			oos.writeObject(o);  // mainly conceived to send a task
			oos.flush();
			Object returnedObject = ois.readObject(); //  mainly conceived to retrieve data produced
			//System.out.println("Returned object :" + returnedObject);
			return returnedObject;
		}
		catch (SocketException e) {
			close();
		}
		
		catch (EOFException e) {
			close();
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public Object send(Object[] objectList) throws IOException {
		try {
			for (Object o : objectList) {
				oos.writeObject(o);
				oos.flush();
				Object returnedObject = ois.readObject(); //  mainly conceived to retrieve data produced
				System.out.println("Returned object :" + returnedObject);
				return returnedObject;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
	}


}
