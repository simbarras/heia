package s16;


public class Queens {
    public interface QueenBoard {
        int boardSize();

        /**
         * true if that (free or occupied) cell is already threatened
         */
        boolean isSquareAttacked(int row, int col);

        void putQueen(int row, int col);

        void removeQueen(int row, int col);

        boolean isQueenThere(int row, int col);
    }
    //=============================================================

    public static boolean isSolvable(QueenBoard b) {
        // b is an 'inout' parameter
        return isSolvableFromColumn(0, b);
    }

    // If a solution is found, returns true and gives the solution
    // in board. Otherwise, returns false and keeps board as it was received.
    public static boolean isSolvableFromColumn(int col,
            /* inout */  QueenBoard board) {
        if (col >= board.boardSize()) return true;
        for (int row = 0; row < board.boardSize(); row++) {
            if (board.isQueenThere(row, col) || board.isSquareAttacked(row, col)) continue;
            board.putQueen(row, col);
            if (!isSolvableFromColumn(col + 1, board)) {
                board.removeQueen(row, col);
            } else {
                return true;
            }
        }
        return false;
    }

    //=============================================================
    static class QueenBoardBasic implements QueenBoard {
        private final boolean[][] hasQueen;
        private final int[] inRow;
        private final int[] inCol;
        private final int[] inDiagonal1;
        private final int[] inDiagonal2;
        private final int size;

        public QueenBoardBasic(int dim) {
            size = dim;
            hasQueen = new boolean[size][size];
            inRow = new int[size];
            inCol = new int[size];
            inDiagonal1 = new int[2 * size - 1];
            inDiagonal2 = new int[2 * size - 1];
        }

        public int boardSize() {
            return size;
        }

        public void putQueen(int row, int col) {
            if (hasQueen[row][col]) throw new IllegalArgumentException("one queen is already there...");
            hasQueen[row][col] = true;
            inRow[row]++;
            inCol[col]++;
            inDiagonal1[row + col]++;
            inDiagonal2[row - col + size - 1]++;
        }

        public void removeQueen(int row, int col) {
            if (!hasQueen[row][col]) throw new IllegalArgumentException("no queen is there...");
            hasQueen[row][col] = false;
            inRow[row]--;
            inCol[col]--;
            inDiagonal1[row + col]--;
            inDiagonal2[row - col + size - 1]--;
        }

        public boolean isSquareAttacked(int row, int col) {
            int neededCount = isQueenThere(row, col) ? 1 : 0;
            return inRow[row] > neededCount
                    || inCol[col] > neededCount
                    || inDiagonal1[row + col] > neededCount
                    || inDiagonal2[row - col + size - 1] > neededCount;
        }

        public String toString() {
            String res = "";
            for (int i = 0; i < size; i++, res += "\n")
                for (int j = 0; j < size; j++)
                    res += (hasQueen[i][j]) ? "X" : "-";
            return res;
        }

        public boolean isQueenThere(int row, int col) {
            return hasQueen[row][col];
        }
    }

    //=============================================================

    private static void checkSolution(QueenBoard b) {
        int n = b.boardSize();
        int queensCount = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!b.isQueenThere(i, j)) continue;
                queensCount++;
                if (b.isSquareAttacked(i, j)) throw new IllegalStateException("attacked queen!");
            }
        }
        if (queensCount != n)
            throw new IllegalStateException("bad number of queens!");
    }

    public static void solve(int n) {
        QueenBoard b = new QueenBoardBasic(n);
        if (isSolvable(b)) {
            System.out.println("Found !\n" + b);
            checkSolution(b);
        } else {
            System.out.println("Not found !");
        }
    }

    public static void main(String[] args) {
        boolean WITH_GUI = false;      // TODO: toggle when measuring performances ;)
        int size = 33;
        if (args.length != 0) size = Integer.parseInt(args[0]);
        if (WITH_GUI) {
            int slowdownMs = 700;
            //QueensGui.main(new String[]{"" + size, "" + slowdownMs});
        } else {
            long start = System.currentTimeMillis();
            System.out.println("Start at: " + start);
            solve(size);
            long finish = System.currentTimeMillis();
            System.out.println("Stop at: " + finish);
            long timeElapsed = finish - start;
            System.out.println("Time: " + timeElapsed);
        }
    }

}
