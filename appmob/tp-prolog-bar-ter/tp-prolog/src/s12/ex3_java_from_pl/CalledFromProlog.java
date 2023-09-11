import java.io.*;

public class CalledFromProlog {
    // "Protocol" :    Request             Output
    //                 -------             ------
    //                 "square" \n int     int
    //                 "bye"               -
    // ------------------------------------------------------------
    public static void main(String[] args) throws IOException {
        BufferedReader kbd = new BufferedReader(new InputStreamReader(System.in));
        String line = kbd.readLine();
        while (!line.equals("bye")) {         // termination request
            if (line.equals("square")) {       // kind of request
                int n = Integer.parseInt(kbd.readLine());
                int n_squared = square(n);
                System.out.println(n_squared);
            } else {
                System.out.println("Unknow command"); // unknown request - ignored
            }
            line = kbd.readLine();
        }
    }

    // ------------------------------------------------------------
    public static int square(int x) {
        return x * x;
    }

}
