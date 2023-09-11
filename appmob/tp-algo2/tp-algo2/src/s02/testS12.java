package s02;

public class testS12 {

    public static void main(String[] args) {
        int[] t = {85, 56, 34, 43, 2, 36, 54, 86};
        int r = 9;
        for (int i = 0; i < t.length; i++) {
            System.out.print(t[i] + ", ");
        }
        System.out.println("R = " + findR(t));
    }

    //La case à la position r donné en paramètre doit être plus petit ou égal au nombre qui se trouve dans la case r+-1
    public static boolean isOkBad(int[] t, int r) {
        int n = t.length;
        if (n == 1) return r == 0;
        if (r == 0) return t[r] <= t[r + 1];
        if (r == n - 1) return t[r] <= t[r - 1];
        return t[r] <= t[r + 1] && t[r] <= t[r - 1];
    }

    //Faire une méthode findR(int[] t) --> trouver r pour isOk donne true.

    public static int findR(int[] t) {
        int n = t.length;
        if (n == 1) return 0;
        if (t[0] <= t[1]) return 0;
        if (t[n - 1] <= t[n - 2]) return n - 1;
        for (int i = 1; i < n - 1; i++) {
            if (t[i] <= t[i + 1]) {
                if (t[i] <= t[i - 1]) return i;
                i++;
            }
        }
        return -1;
    }


    public static int nbOfStars(float[] t) {

        return 1;
    }
}
