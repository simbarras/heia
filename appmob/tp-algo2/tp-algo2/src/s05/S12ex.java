package s05;

public class S12ex {

    public static void main(String[] args) {
        int[] t = {0, 1, 2, 3, 4, 5, 6};
        for (int i = 0; i < t.length; i++) {
            System.out.print(t[i] + " ");
        }
        System.out.println("\n------------------------------");
        rotate(t, 3);

        String tab[] ={"spot", "zut", "pots","stop", "hello" };
        


    }
    public static void rotate(int[] t, int i) {
        int[] result = new int[t.length];
        int k = 0;
        for (int j = 0; j < t.length; j++) {
            if (i < t.length) {
                result[j] = t[i];
                i++;
            } else if (k < i) {
                result[j] = t[k];
                k++;
            }
        }

        for (int j = 0; j < result.length; j++) {
            System.out.print(result[j] + " ");
        }
    }





}
