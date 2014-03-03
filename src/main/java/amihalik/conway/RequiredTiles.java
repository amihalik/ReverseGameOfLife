package amihalik.conway;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.collect.Sets;

/**
 * Utility class that calculates (and caches in the file system) the "required"
 * set of tiles from the "stop" state. This utility can calculate the tiles for
 * any given NxN sized tile set and from the either the "Training" or "Test"
 * results.
 */
public class RequiredTiles {
    public enum Type {
        TEST, TRAIN
    }

    private File cachedir;
    private ProvidedData pd;

    public RequiredTiles(String cachedir, ProvidedData pd) {
        this.cachedir = new File(cachedir);
        this.pd = pd;
    }

    public RequiredTiles() throws IOException {
        this("data/requiredtiles", new ProvidedData());
    }

    /**
     * @param type
     *            either "Test" or "Train"
     * @param window
     *            the tile size. eg. window = 7 will prodice 7x7 tiles.
     * @param tristate
     *            if true, use a third state to mark if a cell in the tile is
     *            within the board.
     * @return
     * @throws IOException
     */
    public Set<String> getRequiredTiles(Type type, int window, boolean tristate) throws IOException {
        String key = getKey(type, window, tristate);
        File datafile = new File(cachedir, key + ".dat");

        if (!datafile.exists()) {
            return createRequiredTilesFile(pd, datafile, type, window, tristate);
        }

        return readRequiredTilesFile(datafile);

    }

    private Set<String> createRequiredTilesFile(ProvidedData pd, File datafile, Type type, int window, boolean tristate)
            throws IOException {
        System.out.println("Creating tiles file " + datafile.getName());

        Map<Integer, GameOfLife> data = null;
        Map<Integer, Integer> deltas = null;

        if (type.equals(Type.TEST)) {
            data = pd.getTestEnd();
            deltas = pd.getTestDelta();
        } else {
            data = pd.getTrainEnd();
            deltas = pd.getTrainDelta();
        }

        Set<String> strs = Sets.newHashSet();

        for (Integer key : data.keySet()) {
            GameOfLife g = data.get(key);
            int delta = deltas.get(key);

            for (int x = 0; x < g.getWidth(); x++) {
                for (int y = 0; y < g.getWidth(); y++) {
                    String tile = g.getTileString(x, y, window, tristate);
                    strs.add(delta + "." + tile);
                }
            }
        }
        FileUtils.writeLines(datafile, strs);

        System.out.println("Done Creating tiles file " + datafile.getName() + ". tile count: " + strs.size());
        return strs;
    }

    private Set<String> readRequiredTilesFile(File datafile) throws IOException {
        System.out.println("Reading tiles file " + datafile.getName());
        Set<String> strs = Sets.newHashSet();

        LineIterator li = FileUtils.lineIterator(datafile);
        while (li.hasNext()) {
            strs.add(li.nextLine());
        }

        System.out.println("Done reading tiles file " + datafile.getName() + ".  tile count: " + strs.size());
        return strs;
    }

    private static String getKey(Type type, int window, boolean tristate) {
        if (tristate) {
            return type + "-" + window + "-tri";
        }
        return type + "-" + window + "-two";

    }

}
