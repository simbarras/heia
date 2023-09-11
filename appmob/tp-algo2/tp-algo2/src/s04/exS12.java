package s04;

import java.util.ArrayList;

public class exS12 {

    public static void main(String[] args) {

        String s = "abc defc defc defgh i";
        int[][] t = {
                {1, 3, 2, 4},
                {2, 8, 1, 4},
                {2, 5, 3, 2},
                {4, 4, 4, 8}
        };

        printArray(t);
        printArray(propagate(t, 8));

        System.out.println("\n" + s);
        findMaxDuplicate(s);

    }

    public static String findMaxDuplicate(String s) {
        char[] charTab = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            charTab[i] = s.charAt(i);
        }


        return "";
    }

    public static int[][] propagate(int[][] t, int v) {
        int rowSize = t.length;
        int colSize = t[0].length;
        int tot = rowSize * colSize;
        System.out.println("\nRow: " + rowSize + " Col: " + colSize + " Tot: " + tot);

        for (int i = 0; i < tot; i++) {

        }


        return null;
    }


    public static void printArray(int[][] t) {
        for (int i = 0; i < t.length; i++) {
            System.out.println();
            for (int j = 0; j < t[i].length; j++) {
                System.out.print(t[i][j] + " ");
            }
        }
    }

}
