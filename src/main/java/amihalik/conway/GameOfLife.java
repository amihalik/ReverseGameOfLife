package amihalik.conway;

import java.util.Arrays;

import com.google.common.base.Preconditions;

/**
 * Manage and process a GameOfLife board.
 */
public class GameOfLife {
    private boolean[][] game;
    private int _xsize;
    private int _ysize;

    public GameOfLife(int xsize, int ysize) {
        game = new boolean[20][20];
    }

    public GameOfLife() {
        this(20, 20);
    }

    public GameOfLife(boolean[][] game) {
        this.game = game;
        this._xsize = game.length;
        this._ysize = game[0].length;

    }

    /**
     * Generate a Random Game of Life Board
     * 
     * @param xsize
     * @param ysize
     * @param pTrue
     *            the probability of a "live" cell appearing
     * @return
     */
    public static GameOfLife getRandomBoard(int xsize, int ysize, double pTrue) {
        boolean[][] rtn = new boolean[xsize][ysize];
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                rtn[x][y] = Math.random() < pTrue;
            }
        }
        return new GameOfLife(rtn);
    }

    public static GameOfLife getRandomBoard(int xsize, int ysize) {
        return getRandomBoard(xsize, ysize, .5);
    }

    public static GameOfLife getRandomBoard() {
        return getRandomBoard(20, 20, .5);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (boolean[] a : game) {
            result = prime * result + Arrays.hashCode(a);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameOfLife other = (GameOfLife) obj;
        if (!Arrays.deepEquals(game, other.game))
            return false;
        return true;
    }

    public int boardsDiffCount(GameOfLife g) {
        boolean[][] b1 = this.game;
        boolean[][] b2 = g.game;

        Preconditions.checkArgument(b1.length == b2.length, "x size not equal");

        int diffCount = 0;
        for (int x = 0; x < b1.length; x++) {
            Preconditions.checkArgument(b1[x].length == b2[x].length, "y size not equal");

            for (int y = 0; y < b1[x].length; y++) {
                if (b1[x][y] != b2[x][y]) {
                    diffCount++;
                }
            }
        }
        return diffCount;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (boolean[] x : game) {
            for (boolean v : x) {
                if (v) {
                    sb.append('1');
                } else {
                    sb.append('0');
                }
                sb.append(",");
            }
        }
        if (sb.length() == 0) {
            return "";
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public static GameOfLife getGameFromOneAndZero(String[] line, int xsize, int ysize) {
        boolean[][] rtn = new boolean[xsize][ysize];
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                rtn[x][y] = line[x * xsize + y].equals("1");
            }
        }
        return new GameOfLife(rtn);
    }

    public String prettyPrint() {
        StringBuffer sb = new StringBuffer();

        for (boolean[] x : game) {
            for (boolean v : x) {
                if (v) {
                    sb.append('*');
                } else {
                    sb.append('-');
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getWidth() {
        return _xsize;
    }

    public int getHiegth() {
        return _ysize;
    }

    public String getTileString(int x, int y, int window, boolean tristate) {
        StringBuffer sb = new StringBuffer();
        int d = window / 2;
        for (int xi = x - d; xi <= (x + d); xi++) {
            for (int yi = y - d; yi <= (y + d); yi++) {
                sb.append(getCharValue(xi, yi, tristate));
            }
        }
        return sb.toString();
    }

    // if (not tristate) false = _, true = *, outside = _;
    // if (tristate) false = _, true = *, outside = x;
    private static final char T = '*';
    private static final char F = '_';
    private static final char O = 'x';

    private char getCharValue(int x, int y, boolean tristate) {
        boolean outside = x < 0 || x >= _xsize || y < 0 || y >= _ysize;

        if (outside) {
            if (tristate) {
                return O;
            } else {
                return F;
            }
        }
        if (game[x][y]) {
            return T;
        } else {
            return F;
        }
    }

    public boolean getValue(int x, int y) {
        return game[x][y];
    }

    private static boolean getValue(int x, int y, boolean[][] game, int _xsize, int _ysize) {
        return x >= 0 && x < _xsize && y >= 0 && y < _ysize && game[x][y];
    }

    private static int getNeighborCount(int x, int y, boolean[][] game, int _xsize, int _ysize) {
        int nc = 0;

        if (getValue(x + 1, y + 1, game, _xsize, _ysize)) {
            nc++;
        }
        if (getValue(x + 1, y, game, _xsize, _ysize)) {
            nc++;
        }
        if (getValue(x + 1, y - 1, game, _xsize, _ysize)) {
            nc++;
        }

        if (getValue(x - 1, y + 1, game, _xsize, _ysize)) {
            nc++;
        }
        if (getValue(x - 1, y, game, _xsize, _ysize)) {
            nc++;
        }
        if (getValue(x - 1, y - 1, game, _xsize, _ysize)) {
            nc++;
        }

        if (getValue(x, y + 1, game, _xsize, _ysize)) {
            nc++;
        }
        if (getValue(x, y - 1, game, _xsize, _ysize)) {
            nc++;
        }

        return nc;
    }

    private static int[][] computeNeighborCount(boolean[][] game, int _xsize, int _ysize) {
        int[][] rtn = new int[_xsize][_ysize];
        for (int x = 0; x < _xsize; x++) {
            for (int y = 0; y < _ysize; y++) {
                rtn[x][y] = getNeighborCount(x, y, game, _xsize, _ysize);
            }
        }
        return rtn;
    }

    public GameOfLife process(int gen) {
        boolean[][] b = game;

        // loops through the generations
        for (int i = 0; i < gen; i++) {

            boolean[][] next = new boolean[_xsize][_ysize];
            int[][] ncs = computeNeighborCount(b, _xsize, _ysize);

            for (int x = 0; x < _xsize; x++) {
                for (int y = 0; y < _ysize; y++) {
                    int nc = ncs[x][y];
                    next[x][y] = (b[x][y] && nc == 2) || nc == 3;
                }
            }
            // System.out.println("gen:" + i);
            // System.out.println(new GameOfLife(next).prettyPrint());
            b = next;
        }
        return new GameOfLife(b);
    }

}