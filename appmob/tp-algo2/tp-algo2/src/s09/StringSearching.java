package s09;

// ------------------------------------------------------------
public class StringSearching {
    // ------------------------------------------------------------
    static /*final*/ int HASHER = 301; // Maybe also try with 7 and 46237
    static /*final*/ int BASE = 256; // Please also try with 257

    // ---------------------
    static int firstFootprint(String s, int len) {
        int hash = 0;
        for (int i = 0; i < len; i++) {
            hash = hash * BASE;
            hash = hash % HASHER + s.charAt(i) % HASHER;
            hash = hash % HASHER;
        }
        return hash;
    }

    // ---------------------
    // must absolutely be O(1)
    // coef is (BASE  power  P.LENGTH-1)  mod  HASHER
    static int nextFootprint(int previousFootprint, char dropChar, char newChar, int coef) {
        int hash = previousFootprint;
        hash = (hash + HASHER - (dropChar * coef) % HASHER); // dropChar
        hash = ((hash % HASHER) * BASE);
        hash = ((hash) % HASHER + newChar % HASHER) % HASHER;
        return hash % HASHER;
    }

    // ---------------------
    // Rabin-Karp algorithm
    public static int indexOf_rk(String t, String p) {
        int length = p.length();
        int pHash = firstFootprint(p, length);
        int tHash = firstFootprint(t, length);
        int coef = 1;
        for (int j = 0; j < length - 1; j++) {
            coef = ((coef % HASHER) * BASE);
        }
        coef %= HASHER;
        for (int i = length; i <= t.length(); i++) {
            if (tHash == pHash) {
                boolean isOk = true;
                for (int j = 0; j < length; j++) {
                    char cT = t.charAt(i - length + j);
                    char cP = p.charAt(j);
                    if (cT != cP) {
                        isOk = false;
                        break;
                    }
                }
                if (isOk) return i - length;
            }
            if (i == t.length()) return -1;
            tHash = nextFootprint(tHash, t.charAt(i - length), t.charAt(i), coef);
        }
        return -1;
    }
}
