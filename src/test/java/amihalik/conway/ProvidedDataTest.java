package amihalik.conway;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import amihalik.conway.ProvidedData;

public class ProvidedDataTest {
    private static String dir = "src/test/resource/provided/";
    private static ProvidedData pd;

    @BeforeClass
    public static void loadData() throws Exception {
        // load the data
        pd = new ProvidedData(dir + "train.csv", dir + "test.csv", 3);
    }

    @Test
    public void testReads() throws Exception {

        Assert.assertEquals(3, pd.getTestEnd().size());
        Assert.assertEquals(3, pd.getTestDelta().size());

        Assert.assertEquals(pd.getTestEnd().get(1).toString(), "0,0,0,0,0,0,0,0,0");
        Assert.assertEquals(pd.getTestEnd().get(2).toString(), "1,1,1,1,1,1,1,1,1");
        Assert.assertEquals(pd.getTestEnd().get(3).toString(), "0,0,0,1,1,1,1,1,1");

        Assert.assertTrue(pd.getTestDelta().get(1).equals(2));
        Assert.assertTrue(pd.getTestDelta().get(2).equals(5));
        Assert.assertTrue(pd.getTestDelta().get(3).equals(1));
    }

    @Test
    public void trainReads() throws Exception {

        Assert.assertEquals(2, pd.getTrainEnd().size());
        Assert.assertEquals(2, pd.getTrainStart().size());
        Assert.assertEquals(2, pd.getTrainDelta().size());

        Assert.assertEquals(pd.getTrainEnd().get(1).toString(), "0,0,0,0,0,0,0,0,0");
        Assert.assertEquals(pd.getTrainEnd().get(2).toString(), "1,1,1,1,1,1,1,1,1");

        Assert.assertEquals(pd.getTrainStart().get(1).toString(), "0,0,0,0,0,0,0,0,0");
        Assert.assertEquals(pd.getTrainStart().get(2).toString(), "0,0,0,0,0,0,0,0,1");

        Assert.assertTrue(pd.getTrainDelta().get(1).equals(2));
        Assert.assertTrue(pd.getTrainDelta().get(2).equals(5));
    }
}
