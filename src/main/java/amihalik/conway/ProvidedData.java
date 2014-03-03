package amihalik.conway;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.collect.Maps;

/**
 * Utility class to read the data provided with the contest. This data is the
 * "training" and "test" files.
 */
public class ProvidedData {
    private Map<Integer, GameOfLife> trainStart = null;
    private Map<Integer, GameOfLife> trainEnd = null;
    private Map<Integer, Integer> trainDelta = null;

    private Map<Integer, GameOfLife> testEnd = null;
    private Map<Integer, Integer> testDelta = null;

    private static final String dir = "data/provided/";

    public ProvidedData() throws IOException {
        this(dir + "train.csv", dir + "test.csv");
    }

    public ProvidedData(String trainfile, String testfile) throws IOException {
        this(trainfile, testfile, 20);
    }

    private void loadTrainData() throws IOException {
        System.out.println("reading training data");
        trainStart = Maps.newHashMap();
        trainEnd = Maps.newHashMap();
        trainDelta = Maps.newHashMap();

        readTrainData(trainfile, size);
        System.out.println("done reading training data");
    }

    private void loadTestData() throws IOException {
        System.out.println("reading test data");
        testEnd = Maps.newHashMap();
        testDelta = Maps.newHashMap();

        readTestData(testfile, size);
        System.out.println("done reading test data");
    }

    private int size;
    private String trainfile;
    private String testfile;

    public ProvidedData(String trainfile, String testfile, int size) {
        this.testfile = testfile;
        this.trainfile = trainfile;
        this.size = size;
    }

    private void readTrainData(String trainfile, int size) throws IOException {
        int totalcells = size * size;
        LineIterator li = null;
        try {
            li = FileUtils.lineIterator(new File(trainfile));

            // skip the header line
            String line = li.nextLine();
            while (li.hasNext()) {
                line = li.nextLine();
                String[] lineSplit = line.replaceAll("\\s+", "").split(",");

                Integer id = Integer.decode(lineSplit[0]);
                Integer delta = Integer.decode(lineSplit[1]);
                trainDelta.put(id, delta);

                String[] start = Arrays.copyOfRange(lineSplit, 2, 2 + totalcells);
                String[] end = Arrays.copyOfRange(lineSplit, 2 + totalcells, 2 + totalcells + totalcells);

                trainStart.put(id, GameOfLife.getGameFromOneAndZero(start, size, size));
                trainEnd.put(id, GameOfLife.getGameFromOneAndZero(end, size, size));
            }
        } finally {
            if (li != null) {
                li.close();
            }
        }
    }

    private void readTestData(String testfile, int size) throws IOException {
        int totalcells = size * size;

        LineIterator li = null;
        try {
            li = FileUtils.lineIterator(new File(testfile));

            // skip the header line
            String line = li.nextLine();
            while (li.hasNext()) {
                line = li.nextLine();
                String[] lineSplit = line.replaceAll("\\s+", "").split(",");

                Integer id = Integer.decode(lineSplit[0]);
                Integer delta = Integer.decode(lineSplit[1]);
                testDelta.put(id, delta);

                String[] end = Arrays.copyOfRange(lineSplit, 2, 2 + totalcells);

                testEnd.put(id, GameOfLife.getGameFromOneAndZero(end, size, size));
            }
        } finally {
            if (li != null) {
                li.close();
            }
        }
    }

    public Map<Integer, GameOfLife> getTrainStart() throws IOException {
        if (trainStart == null) {
            loadTrainData();
        }
        return trainStart;
    }

    public Map<Integer, GameOfLife> getTrainEnd() throws IOException {
        if (trainEnd == null) {
            loadTrainData();
        }
        return trainEnd;
    }

    public Map<Integer, Integer> getTrainDelta() throws IOException {
        if (trainDelta == null) {
            loadTrainData();
        }
        return trainDelta;
    }

    public Map<Integer, GameOfLife> getTestEnd() throws IOException {
        if (testEnd == null) {
            loadTestData();
        }
        return testEnd;
    }

    public Map<Integer, Integer> getTestDelta() throws IOException {
        if (testDelta == null) {
            loadTestData();
        }
        return testDelta;
    }

}
