package rmi.hello;

import java.rmi.*;

public class HelloClient {

    public static void main(String args[]) {

        String host = "localhost"; // or IP address

        try {

            System.out.println("\nPerforming lookup ...");
            Hello obj = (Hello) Naming.lookup("//" + host + "/TheHelloServer");
            Hello obj2 = (Hello) Naming.lookup("//" + host + "/SecondHelloServer");
            System.out.println("Object received:\n" + obj);
            System.out.println("Object received:\n" + obj2);

            System.out.println("\nInvoking remote method ...");
            String message = obj.sayHello();
            System.out.println("Remote method returned '" + message
                    + "' and completed successfully.\n");

            System.out.println("\nInvoking remote method ...");
            String message2 = obj2.sayHello();
            System.out.println("Remote method returned '" + message2
                    + "' and completed successfully.\n");

        } catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
