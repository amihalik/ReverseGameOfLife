package amihalik.conway;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import amihalik.conway.RequiredTiles.Type;

import com.google.common.collect.Maps;

/**
 * Main class used to experiment with the TRAIN files. This class reads in the
 * TRAIN simulation results, tries to solve for the start states for the given
 * end states in the provided TRAIN file, compares the calculated start states
 * with the provided start states, and prints stats to the user.
 */
public class TrainSolver {
    private static Type type = Type.TRAIN;
    private static int window = 7;
    private static boolean tristate = false;
    private static String dir = "data/simulate/";

    public static void main(String[] args) throws IOException {
        int[] miss = new int[5];
        int[] total = new int[5];

        // load the solved tiles.
        Map<String, Boolean> map = Maps.newHashMap();
        connect(map, type, window, tristate);

        // load the training data
        ProvidedData pd = new ProvidedData();

        Map<Integer, GameOfLife> startMap = pd.getTrainStart();
        Map<Integer, GameOfLife> endMap = pd.getTrainEnd();
        Map<Integer, Integer> deltaMap = pd.getTrainDelta();

        for (Integer i : startMap.keySet()) {
            GameOfLife expectStart = startMap.get(i);
            GameOfLife end = endMap.get(i);
            int delta = deltaMap.get(i);

            GameOfLife actualStart = solve(end, delta, map);
            int diffCount = expectStart.boardsDiffCount(actualStart);

            miss[delta - 1] += diffCount;
            total[delta - 1] += 400;
        }

        // Print Stats
        int cMiss = 0;
        int cTotal = 0;
        for (int i = 0; i < 5; i++) {
            int m = miss[i];
            int t = total[i];

            cMiss += m;
            cTotal += t;

            System.out.println("miss    delta = " + (i + 1) + ": " + m);
            System.out.println("total   delta = " + (i + 1) + ": " + t);
            System.out.println("percent delta = " + (i + 1) + ": " + (100. * m / t));
            System.out.println("----");
        }
        System.out.println("miss    = " + cMiss);
        System.out.println("total   = " + cTotal);
        System.out.println("percent = " + (100. * cMiss / cTotal));
        System.out.println("----");

    }

    public static void connect(Map<String, Boolean> map, Type type, int window, boolean tristate) throws IOException {
        System.out.println("loading solver");
        int posCount = 0;
        // load the file
        String simdirname = type + "-" + window + "-" + (tristate ? "tri" : "two") + "-multi/";
        String filedir = dir + simdirname;

        @SuppressWarnings("unchecked")
        Collection<File> files = FileUtils.listFiles(new File(filedir), new String[] { "csv" }, false);

        File f = files.iterator().next();
        System.out.println("loading solver : " + f.getAbsolutePath());
        LineIterator li = FileUtils.lineIterator(f);
        while (li.hasNext()) {
            String line = li.nextLine();
            String[] split = line.split(",");
            String tilekey = split[0];
            double value = Double.parseDouble(split[1]);
            boolean bvalue = value > .5;
            int count = Integer.parseInt(split[2]);
            if (!shouldDrop(tilekey, value, count)) {
                map.put(tilekey, bvalue);
            }
            if (bvalue) {
                posCount++;
            }
        }
        System.out.println("Positive count: " + posCount);
    }

    public static boolean shouldDrop(String tilekey, double value, int count) {
        if (count == 0) {
            return true;
        }
        if (value > .49 && value < .51) {
            return true;
        }
        boolean isFiveByFive = tilekey.startsWith("???????");
        if (!isFiveByFive && count < 5) {
            return true;
        }

        return false;
    }

    public static GameOfLife solve(GameOfLife b, int delta, Map<String, Boolean> map) {
        int xside = b.getWidth();
        int yside = b.getHiegth();
        boolean[][] rtn = new boolean[xside][yside];
        for (int x = 0; x < xside; x++) {
            for (int y = 0; y < yside; y++) {
                String windowString = b.getTileString(x, y, window, tristate);
                String sevenTk = delta + "." + windowString;
                String crossTk = Simulator.compressToCrossTile(sevenTk);
                String fiveTk = Simulator.compressToFiveTile(sevenTk);

                if (map.containsKey(sevenTk)) {
                    rtn[x][y] = map.get(sevenTk);
                } else if (delta > 2) {
                    rtn[x][y] = false;
                } else if (map.containsKey(crossTk)) {
                    rtn[x][y] = map.get(crossTk);
                } else if (map.containsKey(fiveTk)) {
                    rtn[x][y] = map.get(fiveTk);
                } else {
                    rtn[x][y] = false;
                }

            }
        }
        return new GameOfLife(rtn);
    }

}
