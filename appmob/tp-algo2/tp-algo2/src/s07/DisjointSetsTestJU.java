package s07;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

// ======================================================================
public class DisjointSetsTestJU {
    private final int TAB_SIZE = 10;
    private Random rnd = new Random();

    @Test
    public void testToString() {
        for (int i = 0; i < 1000; i++) {
            DisjointSets d = new DisjointSets(TAB_SIZE);
            System.out.println(d.toString());
            for (int j = 0; j < rnd.nextInt(TAB_SIZE) + 1; j++) {
                int a = rnd.nextInt(TAB_SIZE);
                int b = rnd.nextInt(TAB_SIZE);
                d.union(a, b);
                System.out.println("Fusion: " + a + " & " + b + " -> " + d.toString());
            }
            System.out.println();
        }
    }

    @Test
    public void testMinInSame() {
        for (int i = 0; i < 1000; i++) {
            DisjointSets d = new DisjointSets(TAB_SIZE);
            for (int j = 0; j < rnd.nextInt(TAB_SIZE) + 1; j++) {
                d.union(rnd.nextInt(TAB_SIZE), rnd.nextInt(TAB_SIZE));
            }
            for (int j = 0; j < TAB_SIZE; j++) {
                int min = Integer.MAX_VALUE;
                for (int k = 0; k < TAB_SIZE; k++) {
                    if (d.isInSame(j, k)) {
                        min = min > k ? k : min;
                    }
                }
                assertEquals(min, d.minInSame(j));
            }
        }
    }

    // ------------------------------------------------------------
    @Test
    public void testDisjSets() {
        int n = 200;
        for (int j = 1; j < n; j += rnd.nextInt(10) + 1) {
            System.out.print(".");
            testDisjSets(j);
        }
        System.out.println("Test 'union/find/isUnique' passed sucessfully...");
    }

    // ------------------------------------------------------------
    @Test
    public void testEquals() {
        int n = 100;
        for (int j = 1; j < n; j += rnd.nextInt(10) + 1) {
            System.out.print(".");
            testEquals(j);
        }
        System.out.println("Test 'equals' passed sucessfully...");
    }

    //------------------------------------------------------------
    static class DisjSetsNaive {
        private final List<Set<Integer>> sets;

        // --------------------------
        public DisjSetsNaive(int n) {
            sets = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Set<Integer> s = new HashSet<>();
                s.add(i);
                sets.add(s);
            }
        }

        // --------------------------
        public boolean isInSame(int i, int j) {
            for (Set<Integer> s : sets) {
                if (s.contains(i)) return s.contains(j);
            }
            return false;
        }

        // --------------------------
        public void union(int i, int j) {
            if (isInSame(i, j)) return;
            Set<Integer> si = null, sj = null;
            for (Set<Integer> s : sets) {
                if (s.contains(i)) si = s;
                if (s.contains(j)) sj = s;
            }
            si.addAll(sj);
            sets.remove(sj);
        }

        // --------------------------
        public boolean isUnique() {
            return sets.size() == 1;
        }

        // --------------------------
        public boolean equals(Object o) {
            if (!(o instanceof DisjSetsNaive)) return false;
            DisjSetsNaive other = (DisjSetsNaive) o;
            if (other.sets.size() != sets.size()) return false;
            for (Set<Integer> s : sets) {
                boolean found = false;
                for (Set<Integer> os : other.sets)
                    if (s.equals(os)) found = true;
                if (!found) return false;
            }
            return true;
        }

        // --------------------------
        public String toString() {
            return "" + sets;
        }
    }

    // ------------------------------------------------------------
    private void testDisjSets(int n) {
        DisjointSets d = new DisjointSets(n);
        DisjSetsNaive b = new DisjSetsNaive(n);
        int i, j;
        while (!b.isUnique()) {
            assertTrue(!d.isUnique());
            i = rnd.nextInt(n);
            j = rnd.nextInt(n);
            if (rnd.nextBoolean()) {
                b.union(i, j);
                d.union(i, j);
            }
            assertTrue(b.isInSame(i, j) == d.isInSame(i, j));
        }
        assertTrue(d.isUnique());
    }

    // ------------------------------------------------------------
    private void testEquals(int n) {
        DisjointSets d1 = new DisjointSets(n);
        DisjSetsNaive b1 = new DisjSetsNaive(n);
        while (!b1.isUnique()) {
            DisjointSets d2 = new DisjointSets(n);
            int i = rnd.nextInt(n);
            int j = rnd.nextInt(n);
            b1.union(i, j);
            d1.union(i, j);
            if (rnd.nextInt(10) < 3)
                compareEquality(b1, d1, n);
            makeEqual(b1, d2);
            assertTrue(d1.equals(d2));
        }
    }

    // ------------------------------------------------------------
    private void compareEquality(DisjSetsNaive b1, DisjointSets d1, int n) {
        DisjointSets d2 = new DisjointSets(n);
        DisjSetsNaive b2 = new DisjSetsNaive(n);
        while (!b2.isUnique()) {
            int i = rnd.nextInt(n);
            int j = rnd.nextInt(n);
            b2.union(i, j);
            d2.union(i, j);
            assertTrue(b1.equals(b2) == d1.equals(d2));
        }
    }

    // ------------------------------------------------------------
    private void makeEqual(DisjSetsNaive b1, DisjointSets d2) {
        for (Set<Integer> s : b1.sets) {
            List<Integer> l = new ArrayList<>(s);
            Collections.shuffle(l);
            for (int i = 0; i < l.size() - 1; i++)
                d2.union(l.get(i), l.get(i + 1));
        }

    }
}
