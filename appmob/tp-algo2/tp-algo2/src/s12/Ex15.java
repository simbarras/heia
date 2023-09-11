package s12;

public class Ex15 {
    static int nbOfStars(float[] t) {
        int sum = t.length;
        int seq = 1;
        int cutSeq = 0;
        int lvl = 0;
        int cacheLvl = 0;
        for (int i = 1; i < t.length; i++) {
            if (t[i - 1] > t[i]) {
                if (lvl > 1) {
                    cacheLvl = lvl;
                    cutSeq = seq;
                    seq = 0;
                } else if (lvl == 0) {
                    if (--cacheLvl == 0) {
                        seq += cutSeq;
                    }
                    sum += seq;
                }
                lvl = 0;
                seq++;
            } else if (t[i - 1] == t[i]) {
                seq++;
                sum += lvl;
            } else {
                cutSeq = 0;
                cacheLvl = 0;
                sum += ++lvl;
                seq = 1;
            }
        }
        return sum;
    }

    public static int checkMethod(float[] t) {
        int sum = 0;
        if(t.length == 2 && t[0] != t[1]) return 3;
        for (int i = 0; i < t.length; i++) {
            sum += checkMethod(t, i);
        }
        return sum;
    }

    private static int checkMethod(float[] t, int index) {
        if (index <= 0 && index + 2 >= t.length) return 1;
        if (index <= 0) {
            if (t[index] > t[index + 1]) return 1 + checkMethod(t, index + 1, true);
            else if (t[index] == t[index + 1]) return checkMethod(t, index + 1, true);
            else return 1;
        }
        if (index + 1 >= t.length) {
            if (t[index] > t[index - 1]) return 1 + checkMethod(t, index - 1, false);
            else if (t[index] == t[index - 1]) return checkMethod(t, index - 1, false);
            else return 1;
        }
        int left, right;
        if (t[index] > t[index - 1]) left = 1 + checkMethod(t, index - 1, false);
        else if (t[index] == t[index - 1]) left = checkMethod(t, index - 1, false);
        else left = 1;
        if (t[index] > t[index + 1]) right = 1 + checkMethod(t, index + 1, true);
        else if (t[index] == t[index + 1]) right = checkMethod(t, index + 1, true);
        else right = 1;
        return left < right ? right : left;

    }

    private static int checkMethod(float[] t, int index, boolean goRight) {
        if (goRight) {
            if (index + 1 < t.length) {
                if (t[index] > t[index + 1]) return 1 + checkMethod(t, index + 1, true);
                else if (t[index] == t[index + 1]) return checkMethod(t, index + 1, true);
                else return 1;
            } else {
                return 1;
            }
        } else {
            if (index > 0) {
                if (t[index] > t[index - 1]) return 1 + checkMethod(t, index - 1, false);
                else if (t[index] == t[index - 1]) return checkMethod(t, index - 1, false);
                else return 1;
            } else {
                return 1;
            }
        }
    }
}
