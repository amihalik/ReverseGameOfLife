package amihalik.conway;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Utility class (with main) to combine the results of several simulation runs
 * into a single simulation result.
 */
public class CombineSimulatedTiles {
    private static Map<String, Integer> count = Maps.newHashMapWithExpectedSize(17000000);
    private static Map<String, Double> pvalue = Maps.newHashMapWithExpectedSize(17000000);

    private static String inDir = "data/simulate/TEST-7-two-multi/";
    private static String outFile = "data/simulate/TEST-7-two-multi/final.csv";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {

        for (File f : (Collection<File>) FileUtils.listFiles(new File(inDir), new String[] { "csv" }, false)) {
            System.out.println(f.getName());
            LineIterator li = FileUtils.lineIterator(f);
            while (li.hasNext()) {
                addToMap(li.nextLine());
            }
            LineIterator.closeQuietly(li);
        }
        serializeMap();
        determineFillStats();
    }

    public static void addToMap(String line) {
        String[] split = line.split(",");
        String tile = split[0];
        double pRead = Double.parseDouble(split[1]);
        int cRead = Integer.parseInt(split[2]);

        if (!count.containsKey(tile)) {
            count.put(tile, cRead);
            pvalue.put(tile, pRead);
        } else {
            int cOld = count.get(tile);
            double pOld = pvalue.get(tile);

            int cNew = cOld + cRead;
            if (cNew < 0) {
                // Integer rollover, really?
                Preconditions.checkArgument(pOld < .4 || pOld > .6);
                Preconditions.checkArgument(pRead < .4 || pRead > .6);
                boolean bOld = pOld < .4;
                boolean bRead = pRead < .4;
                Preconditions.checkArgument(bOld == bRead);

                // okay, leave as-is
            } else {
                double pNew = 0;
                if (cNew != 0) {
                    pNew = ((cOld * pOld) + (cRead * pRead)) / cNew;
                }
                count.put(tile, cNew);
                pvalue.put(tile, pNew);
            }
        }
    }

    public static void determineFillStats() {
        int[] counts = new int[10];
        int total = 0;
        for (Integer c : count.values()) {
            if (c < 10) {
                counts[c]++;
            }
            total++;
        }
        System.out.println("total: " + total);
        for (int i = 0; i < 10; i++) {
            System.out.println(i + ": " + (100. * counts[i] / total));
        }
    }

    public static void serializeMap() throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(FileUtils.openOutputStream(new File(outFile)));
            for (String key : pvalue.keySet()) {
                int cOld = count.get(key);
                double pOld = pvalue.get(key);
                IOUtils.write(key + "," + pOld + "," + cOld + "\n", out, "UTF-8");
            }
        } finally {
            IOUtils.closeQuietly(out);
        }

    }
}
