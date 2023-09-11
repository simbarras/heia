package s16;

import java.util.Random;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Stack;

public class MaxIt {
    //======================================================================
    static class Move {
        final int row, col;
        final boolean isPlayerA;

        public Move(int row, int col, boolean isPlayerA) {
            this.row = row;
            this.col = col;
            this.isPlayerA = isPlayerA;
        }
    }

    //======================================================================
    static class MinMaxResult {
        final Move chosenMove;
        final int expectedOutcome;

        public MinMaxResult(Move move, int expectedOutcome) {
            this.chosenMove = move;
            this.expectedOutcome = expectedOutcome;
        }
    }

    //======================================================================
    static class Board {
        protected int[][] grid;
        protected boolean[][] isUsed;
        protected int pointsOfA = 0;
        protected int pointsOfB = 0;
        protected int currentRow = 0;
        protected int currentCol = 0;
        protected int usedCells = 0;
        private Stack<Move> moves = new Stack<>();

        public Board(int dimension) {
            grid = new int[dimension][dimension];
            isUsed = new boolean[dimension][dimension];
        }

        public int dimension() {
            return grid.length;
        }

        public boolean isValidMove(Move m) {
            if (m.col < 0 || m.col > grid.length - 1) return false;
            if (m.row < 0 || m.row > grid[m.col].length - 1) return false;
            if (isUsed[m.col][m.row]) return false;
            if (usedCells == 0) return true;
            return (m.col == currentCol) || (m.row == currentRow);
        }

        public Move[] possibleMoves(boolean isPlayerA) {
            Move[] res;
            int i = 0;
            int j = 0;
            int n = 0;
            Stack<Move> s = new Stack<>();
            Move m;

            for (i = 0; i < grid.length; i++) {
                m = new Move(i, currentCol, isPlayerA);
                if (isValidMove(m)) {
                    n++;
                    s.push(m);
                }
            }
            for (j = 0; j < grid[0].length; j++) {
                m = new Move(currentRow, j, isPlayerA);
                if (isValidMove(m)) {
                    n++;
                    s.push(m);
                }
            }
            res = new Move[n];
            while (!s.empty()) {
                res[--n] = s.pop();
            }
            return res;
        }

        public void play(Move m) {
            moves.push(m);
            usedCells++;
            isUsed[m.col][m.row] = true;
            if (m.isPlayerA)
                pointsOfA += grid[m.col][m.row];
            else
                pointsOfB += grid[m.col][m.row];
            currentCol = m.col;
            currentRow = m.row;
        }

        public void undo() {
            Move m = moves.pop();
            usedCells--;
            isUsed[m.col][m.row] = false;
            int score = grid[m.col][m.row];
            if (m.isPlayerA) {
                pointsOfA -= score;
            } else {
                pointsOfB -= score;
            }
            m = moves.peek();
            currentCol = m.col;
            currentRow = m.row;
        }

        /**
         * The "measure" according to the general MiniMax model
         */
        public int score() {
            return pointsOfA - pointsOfB;
        }

        public boolean isGameOver() {
            return possibleMoves(false).length == 0;
        }

        public String toString() {
            String res = "";
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (isUsed[i][j]) {
                        if (i == currentCol && j == currentRow) {
                            res += " !!";
                        } else {
                            res += " --";
                        }
                    } else {
                        res += " " + grid[i][j];
                    }
                }
                res += "\n";
            }
            res += "          A: " + pointsOfA + ",     B:" + pointsOfB;
            res += "\n";
            return res;
        }

        public static Board rndBoard(int dim) {
            Board b = new Board(dim);
            for (int i = 0; i < dim; i++)
                for (int j = 0; j < dim; j++) {
                    b.grid[i][j] = rnd.nextInt(90) + 10;
                }
            return b;
        }

        public static Board rndBoard(int dim, int seed) {
            rnd = new Random(seed);
            return rndBoard(dim);
        }
    }
    //======================================================================

    public static Move readMove(Board b, boolean isPlayerA) {
        Move m = null;
        try {
            do {
                m = enterMove(isPlayerA);
            } while (!b.isValidMove(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return m;
    }

    private static Move enterMove(boolean isPlayerA) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(isPlayerA ? "PlayerA" : "PlayerB");
        System.out.print(", enter row and column: ");
        String s = is.readLine();
        StringTokenizer st = new StringTokenizer(s);
        int row = Integer.parseInt(st.nextToken());
        int col = Integer.parseInt(st.nextToken());
        return new Move(row, col, isPlayerA);
    }

    public static Move rndMove(Board board, boolean isPlayerA) {
        Move[] mt = board.possibleMoves(isPlayerA);
        int i = rnd.nextInt(mt.length);
        return mt[i];
    }

    public static Move greedyMove(Board board, boolean isPlayerA) {
        Move[] mt = board.possibleMoves(isPlayerA);
        Move bestMove = mt[0];
        int best = board.grid[bestMove.col][bestMove.row];
        for (Move m : mt) {
            int x = board.grid[m.col][m.row];
            if (x > best) {
                best = x;
                bestMove = m;
            }
        }
        return bestMove;
    }

    static Move bestMove(Board board, boolean isPlayerA, int levels) {
        MinMaxResult outcome = expectedScore(board, isPlayerA, levels);
        return outcome.chosenMove;
    }

    /**
     * @return the expected score, as well as the move that
     * has been chosen by the MiniMax algorithm (null when game is over),
     * limited to a computation depth of "levels".
     */
    static MinMaxResult expectedScore(Board board, boolean isPlayerA,
                                      int levels) {
        // "terminal" configuration
        if (board.isGameOver())
            return new MinMaxResult(null, board.score());

        Move[] mt = board.possibleMoves(isPlayerA);

        // "quasi-terminal" configuration
        if (levels == 0) {
            // mt[0] is just to announce something (probably the caller won't use
            return new MinMaxResult(mt[0], board.score());   // it anyway!)
        }

        MinMaxResult prevRes;
        MinMaxResult bestMove = new MinMaxResult(mt[0], board.score());
        int score = isPlayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < mt.length; i++) {
            board.play(mt[i]);
            prevRes = expectedScore(board, !isPlayerA, levels - 1);
            if (isPlayerA == (score < prevRes.expectedOutcome)) {
                score = prevRes.expectedOutcome;
                bestMove = new MinMaxResult(mt[i], score);
            }
            board.undo();
        }
        return bestMove;
    }

    static Move bestMoveAlphaBeta(Board board, boolean isPlayerA, int levels) {
        return expectedScore(board, isPlayerA, levels,
                Integer.MIN_VALUE, Integer.MAX_VALUE).chosenMove;
    }

    public static MinMaxResult expectedScore(Board board, boolean isPlayerA,
                                             int levels, int alpha, int beta) {

        // "terminal" configuration
        if (board.isGameOver())
            return new MinMaxResult(null, board.score());

        Move[] mt = board.possibleMoves(isPlayerA);

        // "quasi-terminal" configuration
        if (levels == 0) {
            // mt[0] is just to announce something (probably the caller won't use
            return new MinMaxResult(mt[0], board.score());   // it anyway!)
        }

        MinMaxResult prevRes;
        MinMaxResult bestMove = new MinMaxResult(mt[0], board.score());
        int score = isPlayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < mt.length; i++) {
            board.play(mt[i]);
            prevRes = expectedScore(board, !isPlayerA, levels - 1, alpha, beta);
            if (isPlayerA && score < prevRes.expectedOutcome) {
                score = prevRes.expectedOutcome;
                alpha = prevRes.expectedOutcome;
                bestMove = new MinMaxResult(mt[i], score);
                if (beta <= score) i = mt.length;
            } else if (!isPlayerA && score > prevRes.expectedOutcome) {
                score = prevRes.expectedOutcome;
                beta = prevRes.expectedOutcome;
                bestMove = new MinMaxResult(mt[i], score);
                if(alpha >= score) i = mt.length;
            }
            board.undo();
        }
        return bestMove;

    }

    // ------------------------------------------------------------

    public static void playGame(int levels, int boardSize) {
        Board board = Board.rndBoard(boardSize, 12);
        //Board board = Board.rndBoard(boardSize);
        boolean isPlayerA = true;
        Move move;
        long t1 = System.nanoTime();
        while (!board.isGameOver()) {

            // TODO: comment that println() when testing performance!
            System.out.println(board);
            if (isPlayerA) {
                //move = bestMoveAlphaBeta(board, isPlayerA, levels);
                move = rndMove(board, isPlayerA);
                //move = greedyMove(board, isPlayerA);
                //move = readMove(board, isPlayerA);
                //move = bestMove(board, isPlayerA, levels);
            } else {
                //move = readMove(board, isPlayerA);
                //move = rndMove(board, isPlayerA);
                //move = greedyMove(board, isPlayerA);
                //move = bestMove(board, isPlayerA, levels);
                move = bestMoveAlphaBeta(board, isPlayerA, levels);
            }
            board.play(move);
            isPlayerA = !isPlayerA;
        }
        long t2 = System.nanoTime();
        System.out.println("total time: " + (t2 - t1) / 1000 / 1000 + " [ms]");
        System.out.println(board);
        System.out.println("Game over");
    }

    // ------------------------------------------------------------
    private static Random rnd = new Random();

    // ------------------------------------------------------------
    public static void main(String[] args) {
        int levels = 8;
        int boardSize = 6;
        if (args.length == 2) {
            levels = Integer.parseInt(args[0]);
            boardSize = Integer.parseInt(args[1]);
        }
        playGame(levels, boardSize);
    }
    // ------------------------------------------------------------
    // Il n'y a pas de programme test. La stratégie miniMax (B) devrait gagner
    // nettement contre la version "random" (A). Voici ce que j'obtiens
    // avec level=5 et boardSize=6   ------>     A: 688,     B:1052
}
