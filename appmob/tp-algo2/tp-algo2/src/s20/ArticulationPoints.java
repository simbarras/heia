package s20;

import java.util.*;
import java.util.stream.Stream;

public class ArticulationPoints {
    // =============================================================
    static class ArtData {
        int preOrderCounter = 0;
        int[] noAnc, noPre;
        boolean[] isArtPoint, isVisited;
        int nbOfRootSons = 0;
        int rootVid;

        ArtData(int nVertices, int root) {
            noPre = new int[nVertices];
            noAnc = new int[nVertices];
            isVisited = new boolean[nVertices];
            isArtPoint = new boolean[nVertices];
            rootVid = root;
        }
    }

    // =============================================================
    private static void findArt(UGraph g, int vid, ArtData art) {
        art.isVisited[vid] = true;
        art.noAnc[vid] = art.noPre[vid] = art.preOrderCounter++;
        for (Integer neighbour : g.neighboursOf(vid)) {
            if (!art.isVisited[neighbour]) {
                if (vid == art.rootVid) art.nbOfRootSons++;
                findArt(g, neighbour, art);
                if (art.noAnc[neighbour] >= art.noPre[vid] && vid != art.rootVid) {
                    art.isArtPoint[vid] = true;
                }
                art.noAnc[vid] = Math.min(art.noAnc[vid], art.noAnc[neighbour]);
            } else {
                art.noAnc[vid] = Math.min(art.noAnc[vid], art.noPre[neighbour]);
            }
        }
    }

    public static Set<Integer> articulationPoints(UGraph g, int startVid) {
        ArtData art = new ArtData(g.nbOfVertices(), startVid);
        art.rootVid = startVid;
        findArt(g, startVid, art);
        Set<Integer> result = new TreeSet<>();
        for (int i = 0; i < art.isArtPoint.length; i++) {
            if (art.isArtPoint[i]) result.add(i);
        }
        if (art.nbOfRootSons > 1) result.add(startVid);
        return result;
    }

    public static Set<Integer> articulationPoints(UGraph g) {
        if (g.nbOfVertices() == 0) return new HashSet<>();
        return articulationPoints(g, 0);
    }

    // ------------------------------------------------------------
    public static void main(String[] args) {
        int j;
        final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12;
        int nVertices = 13;
        int[] srcs = {A, A, A, A, C, D, D, E, E, G, G, G, H, J, J, J, L};
        int[] dsts = {F, C, B, G, G, E, F, F, G, L, J, H, I, K, L, M, M};
        UGraph g = new UGraph(nVertices, srcs, dsts);
        System.out.println(g + "\n");
        Set<Integer> art = articulationPoints(g, D);
        System.out.print("Points d'articulation: " + art);
        System.out.println();
        for (j = 0; j < nVertices; j++) {
            Set<Integer> res = articulationPoints(g, j);
            System.out.print("[");
            for (int r : res) {
                System.out.print(r + ", ");
            }
            System.out.println("]");
            if (!res.equals(art)) {
                System.out.println("Error with root: " + j);
                throw new RuntimeException("not the same result for different starting nodes");
            }
        }
    }
}
