package rmi.hello;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class HelloServer extends UnicastRemoteObject implements Hello {

    public HelloServer() throws RemoteException {
        super();
    }

    public String sayHello() throws RemoteException {
        System.out.println("- saying hello to all that may ask ...");
        return "Hi Folks!!";
    }

    public static void main(String args[]) {
        try {

            System.out.println("\nLocally launching RMI registry ...");
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry is running");

            HelloServer obj = new HelloServer();
            HelloServer obj2 = new HelloServer();

            System.out.println("\nContacting registry on localhost ...");
            Naming.rebind("//localhost/TheHelloServer", obj);
            Naming.rebind("//localhost/SecondHelloServer", obj2);
            System.out.println("HelloServer bound in registry.\n\nStop server using Ctrl-C.");

        } catch (Exception e) {

            System.out.println("HelloServer exception: " + e.getMessage());
            e.printStackTrace();

        }
    }
}
