package s12;

public class Ex02 {


    /**
     * This solution is a naive implementation.
     * RAM: O(n)
     * CPU: O(n)
     *
     * @param t     initial tab
     * @param pivot
     * @return rotated tab
     */
    public static int[] rotateNaive1(int[] t, int pivot) {
        int[] result = new int[t.length];
        for (int i = 0; i < t.length; i++) {
            result[i] = t[(i + pivot) % t.length];
        }
        return result;
    }

    /**
     * This solution is a naive implementation.
     * RAM: O(1)
     * CPU: O(n^2)
     *
     * @param t     initial tab
     * @param pivot
     * @return rotated tab
     */
    public static int[] rotateNaive2(int[] t, int pivot) {
        for (int i = 0; i < pivot; i++) {
            for (int j = 1; j < t.length; j++) {
                int tmp = t[j];
                t[j] = t[j - 1];
                t[j - 1] = tmp;
            }
        }
        return t;
    }

    /**
     * This solution is the best implementation.
     * RAM: O(1)
     * CPU: O(n)
     * Mix of the two naive solution
     *
     * @param t     initial tab
     * @param pivot
     * @return rotated tab
     */
    public static int[] rotate(int[] t, int pivot) {
        if (pivot == 0) return t;
        if (t.length < 2) return t;
        for (int i = 0; i < (pivot) / 2; i++) {
            int tmp = t[i];
            t[i] = t[pivot - 1 - i];
            t[pivot - 1 - i] = tmp;
        }
        for (int i = 0; i < (t.length - pivot) / 2; i++) {
            int tmp = t[pivot + i];
            t[pivot + i] = t[t.length - 1 - i];
            t[t.length - 1 - i] = tmp;
        }
        for (int i = 0; i < t.length / 2; i++) {
            int tmp = t[i];
            t[i] = t[t.length - 1 - i];
            t[t.length - 1 - i] = tmp;
        }
        return t;

    }

    public static void main(String[] args) {
        System.out.println("Rotate a tab");
    }
}
