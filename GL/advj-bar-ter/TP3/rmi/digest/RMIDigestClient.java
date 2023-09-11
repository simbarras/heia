package rmi.digest;

import java.rmi.Naming;
import java.util.Date;

public class RMIDigestClient {

    final static String host = "160.98.34.19"; // or IP address
    final static String serverMethod = "RMIDigestServer";

    final static String username = "barter";
    //final static String password = "73df1308ebd486b6db49844bab27e113";
    final static String password = "petitcoquin";

    public static void main(String[] args) {
        try {
            System.out.println("\nPerforming lookup ...");
            RMIDigestValidator obj = (RMIDigestValidator) Naming.lookup("//" + host + "/" + serverMethod);
            System.out.println("Object received:\n" + obj);

            MD5Digest md5 = new MD5Digest();

            byte[] challenge = obj.getChallenge(username).getBytes();

            System.out.println("Challenge: " + new String(challenge));

            byte[] pwdHash = md5.doHash(password);

            byte[] hash = md5.doHash(pwdHash, challenge);

            System.out.println("Hash: " + new String(hash));

            boolean result = obj.challengeResponse(username, hash);

            // If result is true, the password is correct and print the date time of the authentication
            if (result) {
                System.out.println("Authentication successful at " + new Date());
            } else {
                System.out.println("Authentication failed " + new Date());
            }


        } catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
