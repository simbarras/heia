package s12;

public class Ex04 {
    public int[] maxMin(int[] t) {
        int[] i = cdm(t, 0, t.length - 1);
        return new int[]{i[0], i[1]};
    }

    private int[] cdm(int[] t, int l, int k) {
        if (l == k) {
            return new int[]{t[l], t[l]};
        }
        if (k - l == 1) {
            return t[l] < t[k] ? new int[]{t[l], t[k]} : new int[]{t[k], t[l]};
        }
        int[] r1 = cdm(t, l, ((l + k) / 2) - 1);
        int[] r2 = cdm(t, (l + k) / 2, k);
        return new int[]{r1[0] < r2[0] ? r1[0] : r2[0], r1[1] > r2[1] ? r1[1] : r2[1]};
    }
}
