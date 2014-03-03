package amihalik.conway;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import amihalik.conway.RequiredTiles.Type;

import com.google.common.collect.Maps;

/**
 * Main class used to create the submissions to Kaggle. This class reads in the
 * TEST simulation results, tries to solve for the start states for the given
 * end states in the provided TEST file, and serializes the results in the
 * required format.
 */
public class TestSolver {
    private static Type type = Type.TEST;
    private static int window = 7;
    private static boolean tristate = false;
    private static String dir = "data/simulate/";

    public static void main(String[] args) throws IOException {
        File outputname = new File("sub/Final-cr5-8x.csv");
        List<String> outputlines = new ArrayList<String>(50005);

        outputlines.add(getHeaderLine());

        // load the solved tiles.
        Map<String, Boolean> map = Maps.newHashMap();
        connect(map, type, window, tristate);

        // load the TEST data
        ProvidedData pd = new ProvidedData();

        Map<Integer, GameOfLife> endMap = pd.getTestEnd();
        Map<Integer, Integer> deltaMap = pd.getTestDelta();

        for (Integer id : endMap.keySet()) {
            GameOfLife end = endMap.get(id);
            int delta = deltaMap.get(id);

            GameOfLife actualStart = solve(end, delta, map);

            outputlines.add(id + "," + actualStart);
        }
        FileUtils.writeLines(outputname, outputlines);

    }

    public static String getHeaderLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("id");
        for (int i = 1; i <= 400; i++) {
            sb.append(",start." + i);
        }
        return sb.toString();
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
            long count = Long.parseLong(split[2]);
            if (!shouldDrop(tilekey, value, count)) {
                map.put(tilekey, bvalue);
            }
            if (bvalue) {
                posCount++;
            }
        }
        System.out.println("Positive count: " + posCount);
    }

    public static boolean shouldDrop(String tilekey, double value, long count) {
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

                // solve(step1:7+5, step2:7+5, step3:7+, step4:7+, step5:7)
                if (map.containsKey(sevenTk)) {
                    rtn[x][y] = map.get(sevenTk);
                } else if (map.containsKey(crossTk)) {
                    rtn[x][y] = map.get(crossTk);
                } else if (delta == 5) {
                    rtn[x][y] = false;
                } else if (delta > 2) {
                    rtn[x][y] = false;
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
