package amihalik.conway;

import java.util.Set;

import com.google.common.collect.Sets;

public class TileSymmetry {

    /**
     * Calculate all of the symmetric tiles for a given tile string
     * 
     * @param tile
     * @return
     */
    public static Set<String> calcSymmetricTiles(String tile) {
        Set<String> rtn = Sets.newHashSet();
        rtn.add(tile);
        for (char[][] rot : rotate(strToArray(tile))) {
            for (char[][] flip : flip(rot)) {
                rtn.add(arrayToString(flip));
            }

        }
        return rtn;
    }

    public static Set<char[][]> flip(char[][] in) {
        int n = in.length;
        Set<char[][]> rtn = Sets.newHashSet();
        rtn.add(in);
        char[][] other = new char[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                other[x][y] = in[n - 1 - x][y];
            }
        }
        rtn.add(other);
        return rtn;
    }

    public static Set<char[][]> rotate(char[][] in) {
        Set<char[][]> rtn = Sets.newHashSet();
        rtn.add(in);

        char[][] temp = rot(in);
        rtn.add(temp);

        temp = rot(temp);
        rtn.add(temp);

        temp = rot(temp);
        rtn.add(temp);

        return rtn;
    }

    public static char[][] rot(char[][] in) {
        int n = in.length;
        char[][] ret = new char[n][n];
        for (int x = 0; x < n; ++x) {
            for (int y = 0; y < n; ++y) {
                ret[x][y] = in[n - y - 1][x];
            }
        }
        return ret;
    }

    public static char[][] strToArray(String tile) {
        int n = (int) Math.sqrt(tile.length());
        char[][] rtn = new char[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                rtn[x][y] = tile.charAt(x * n + y);
            }
        }
        return rtn;
    }

    public static String arrayToString(char[][] tile) {
        int n = tile.length;
        StringBuffer sb = new StringBuffer(50);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                sb.append(tile[x][y]);
            }
        }
        return sb.toString();
    }

}
