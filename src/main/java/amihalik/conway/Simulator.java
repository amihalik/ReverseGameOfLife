package amihalik.conway;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import amihalik.conway.RequiredTiles.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Main used for running Game of Life simulations. This class stores the "count"
 * of each time a cell was alive for a given stop tile, and the total number of
 * times a tile was observed.
 * 
 * After the simulation completes, these results are serialized out to disk.
 * 
 * This file is configured to sample 7x7 tiles (49 bits), 7x7 with a "cross"
 * filter (37 bits), and 5x5 (25 bits).
 */
public class Simulator {

    private static Type type = Type.TEST;
    private static int window = 7;
    private static boolean tristate = false;
    private static String resultsDir = "data/simulate/";

    // number of random board to simulate
    private static int simulation_count = 1660000;

    private static NumberFormat NF = NumberFormat.getNumberInstance(Locale.US);

    public static void main(String[] str) throws Exception {
        // create a cache
        Map<String, Integer> index = Maps.newHashMap();

        // load the data
        System.out.println("filling the cache");
        RequiredTiles rt = new RequiredTiles();
        Set<String> tiles = rt.getRequiredTiles(type, window, tristate);
        System.out.println("Basic tile count: " + NF.format(tiles.size()));
        expandRequiredTiles(tiles);
        System.out.println("Expanded tile count: " + NF.format(tiles.size()));

        int[] counts = new int[tiles.size()];
        int[] totals = new int[tiles.size()];

        int j = 0;
        for (String tile : tiles) {
            index.put(tile, j);
            j++;
        }
        System.out.println("done filling the cache");

        // does this help?
        tiles.clear();
        tiles = null;
        System.gc();

        // run
        long start = System.currentTimeMillis();
        for (int i = 0; i < simulation_count; i++) {
            runOnce(index, counts, totals);
            if (i % 1000 == 0) {
                long current = System.currentTimeMillis();
                System.out.println(NF.format(i) + ": " + (current - start) / 1000.);
                start = current;
            }
        }

        System.out.println("starting fill stats");
        determineFillStats(totals);

        System.out.println("shutting down");

        String filename = System.currentTimeMillis() + ".csv";
        String simdirname = type + "-" + window + "-" + (tristate ? "tri" : "two") + "-multi/";
        String filepath = resultsDir + simdirname + filename;

        FileUtils.touch(new File(filepath));
        serializeMap(filepath, index, counts, totals);
        System.out.println("done");
    }

    public static String[] doubleTiles(String b) {
        String prefix = b.substring(0, 2);
        String tile = b.substring(2, 51);
        String[] rtn = new String[] { b, prefix + StringUtils.reverse(tile) };
        return rtn;
    }

    public static String[] expandRequiredTiles(String b) {
        String[] rtn = new String[3];
        rtn[0] = b;
        rtn[1] = compressToCrossTile(b);
        rtn[2] = compressToFiveTile(b);
        return rtn;
    }

    private static void expandRequiredTiles(Set<String> basic) {
        Set<String> temp = Sets.newHashSetWithExpectedSize(basic.size() * 2);
        for (String b : basic) {
            temp.add(compressToCrossTile(b));
            temp.add(compressToFiveTile(b));
        }
        basic.addAll(temp);
    }

    public static void serializeMap(String filename, Map<String, Integer> index, int[] counts, int[] totals)
            throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filename));
            for (String key : index.keySet()) {
                int count = counts[index.get(key)];
                int total = totals[index.get(key)];
                double p = 0;
                if (total > 0) {
                    p = 1. * count / total;
                }
                out.write(key + "," + p + "," + total + "\n");
            }

        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    public static void determineFillStats(int[] totals) {
        int[] counts = new int[10];
        int total = 0;
        for (int d : totals) {
            if (d < 10) {
                counts[d]++;
            }
            total++;
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(i + ": " + (100. * counts[i] / total));
        }
    }

    public static void runOnce(Map<String, Integer> index, int[] counts, int[] totals) {
        GameOfLife b = GameOfLife.getRandomBoard();
        GameOfLife start = b.process(5);

        GameOfLife current = start;
        for (int step = 1; step <= 5; step++) {
            current = current.process(1);

            // Do I need any of these tiles?
            for (int x = 0; x < current.getWidth(); x++) {
                for (int y = 0; y < current.getHiegth(); y++) {
                    String calculatedtile = current.getTileString(x, y, window, tristate);

                    // TODO use 8-way symmetry, not just two way
                    for (String tile : new String[] { calculatedtile, StringUtils.reverse(calculatedtile) }) {
                        String k = step + "." + tile;
                        for (String tileKey : expandRequiredTiles(k)) {
                            if (index.containsKey(tileKey)) {
                                int i = index.get(tileKey);
                                totals[i]++;

                                if (start.getValue(x, y)) {
                                    counts[i]++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static final int[] CROSS = new int[] { 2, 3, 7, 8, 9, 15, 37, 43, 44, 45, 49, 50 };
    private static final int[] FIVE = new int[] { 2, 3, 4, 5, 6, 7, 8,//
            9, 16, 23, 30, 37, //
            15, 22, 29, 36, 43,//
            44, 45, 46, 47, 48, 49, 50 };

    public static String compressToCrossTile(String tilekey) {
        return compress(tilekey, CROSS);
    }

    public static String compressToFiveTile(String tilekey) {
        return compress(tilekey, FIVE);
    }

    private static String compress(String tilekey, final int[] unk) {
        Preconditions.checkArgument(tilekey.length() == 51);
        StringBuffer sb = new StringBuffer(tilekey);
        for (int i : unk) {
            sb.setCharAt(i, '?');
        }
        return sb.toString();
    }

}
