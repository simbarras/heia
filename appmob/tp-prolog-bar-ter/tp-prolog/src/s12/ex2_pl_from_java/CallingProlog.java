import java.io.*;

public class CallingProlog {
  static BufferedReader fromProlog;
  static PrintWriter    toProlog;
  static final String INTERP_NAME = "gprolog";
  static final String   FILE_NAME = "called_from_java";  // in current directory
  // -----------------------------------------------------------------
  public static void main(String[] args) throws IOException{
    startProlog();
    System.out.println(square(3));
    System.out.println(square(6));
    closeProlog();
  }
  // -----------------------------------------------------------------
  static int square(int n) throws IOException {
    String cmd = "mySquare(" + n + ", M).";
    send(cmd);
    String line = fromProlog.readLine();
    while(! line.startsWith("M = ")) {
      line = fromProlog.readLine();          // purge non-result lines
    }
    int res = 0;
    try {
      res = Integer.parseInt(line.substring(4));
    } catch (NumberFormatException e) {
      throw new IOException(e.getMessage());
    }
    return res;
  }
  // -----------------------------------------------------------------
  public static void send(String s) throws IOException {
    toProlog.println(s); 
    toProlog.flush();
  }
  // -----------------------------------------------------------------
  public static void startProlog() throws IOException {
    String[] args = {INTERP_NAME};
    ProcessBuilder pb = new ProcessBuilder(args);
    pb.environment().put("LINEDIT","gui=no");  // no window on Win32
    Process prcss = null;
    try {
      prcss = pb.start();
    } catch (IOException e) {
      System.out.println("Problème de lancement de gprolog: " + e);
    }
    fromProlog = new BufferedReader(new InputStreamReader(prcss.getInputStream()));
    toProlog = new PrintWriter(prcss.getOutputStream());
    send("[" + FILE_NAME + "].");  // to get the file "consulted"
  }
  // -----------------------------------------------------------------
  static void closeProlog() throws IOException {
    send("halt.");                 // built-in predicate to quit
    fromProlog.close();
    toProlog.close();
  }
  // -----------------------------------------------------------------
}
