//---------------------------------------------
// ShowWinInfo - uses native library to get
//               Windows version information
//---------------------------------------------
// 2023-03-01 R. Scheurer (HEIA-FR)
//---------------------------------------------


public class ShowWinInfo {

    static {
        System.loadLibrary("wininfo");
    }

    public static native WinInfo getWinInfo();

    public static void main(String[] args) {

        WinInfo wi = getWinInfo();

        //*** DO NOT MODIFY THE FOLLOWING LINE !
        System.out.println("\nOperating System:\n" + wi.print());
    }

}

