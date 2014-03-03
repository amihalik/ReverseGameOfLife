package amihalik.conway;

import java.io.File;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import amihalik.conway.ProvidedData;
import amihalik.conway.RequiredTiles;
import amihalik.conway.RequiredTiles.Type;

public class RequiredTilesTest {
    private static String dir = "src/test/resource/provided/";
    private static String tilesdir = "src/test/resource/requiredtiles/";
    private static ProvidedData pd;
    private static RequiredTiles rt;

    @BeforeClass
    public static void loadData() throws Exception {
        // clear the directory
        FileUtils.deleteDirectory(new File(tilesdir));

        // load the data
        pd = new ProvidedData(dir + "train.csv", dir + "test.csv", 3);
        rt = new RequiredTiles(tilesdir, pd);
    }

    @Test
    public void testWindow() throws Exception {

        // Assert.assertEquals(pd.getTrainStart().get(1).toString(),
        // "0,0,0,0,0,0,0,0,0");

        Assert.assertEquals("xxxx__x__", pd.getTrainStart().get(1).getTileString(0, 0, 3, true));
        Assert.assertEquals("_________", pd.getTrainStart().get(1).getTileString(0, 0, 3, false));

        Assert.assertEquals("xxxxxxxxxxxx___xx___xx___", pd.getTrainStart().get(1).getTileString(0, 0, 5, true));
        Assert.assertEquals("_________________________", pd.getTrainStart().get(1).getTileString(0, 0, 5, false));

        // Assert.assertEquals(pd.getTestEnd().get(3).toString(),
        // "0,0,0,1,1,1,1,1,1");
        Assert.assertEquals("xxxx__x**", pd.getTestEnd().get(3).getTileString(0, 0, 3, true));
        Assert.assertEquals("_______**", pd.getTestEnd().get(3).getTileString(0, 0, 3, false));

        // Assert.assertEquals(pd.getTrainStart().get(2).toString(),
        // "0,0,0,0,0,0,0,0,1");
        Assert.assertEquals("__x_*xxxx", pd.getTrainStart().get(2).getTileString(2, 2, 3, true));
        Assert.assertEquals("____*____", pd.getTrainStart().get(2).getTileString(2, 2, 3, false));
        Assert.assertEquals("*", pd.getTrainStart().get(2).getTileString(2, 2, 1, false));

    }

    @Test
    public void testRequredTiles() throws Exception {

        // train (start) end
        // 1,2, 0,0,0, 0,0,0, 0,0,0, -- 0,0,0, 0,0,0, 0,0,0
        // 2,5, 0,0,0, 0,0,0, 0,0,1, -- 1,1,1, 1,1,1, 1,1,1

        Set<String> rts = rt.getRequiredTiles(Type.TRAIN, 1, true);
        Assert.assertEquals(2, rts.size());
        Assert.assertTrue(rts.contains("2._"));
        Assert.assertTrue(rts.contains("5.*"));

        // 1,2, 0,0,0, 0,0,0, 0,0,0
        // 2,5, 1,1,1, 1,1,1, 1,1,1
        // 3,1, 0,0,0, 1,1,1, 1,1,1

        rts = rt.getRequiredTiles(Type.TEST, 1, true);
        Assert.assertEquals(4, rts.size());
        Assert.assertTrue(rts.contains("2._"));
        Assert.assertTrue(rts.contains("5.*"));
        Assert.assertTrue(rts.contains("1._"));
        Assert.assertTrue(rts.contains("1.*"));

        rts = rt.getRequiredTiles(Type.TEST, 3, false);
        Assert.assertEquals(19, rts.size());
        Assert.assertTrue(rts.contains("2._________"));

        Assert.assertTrue(rts.contains("5.___ _** _**".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.___ *** ***".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.___ **_ **_".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5._** _** _**".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.*** *** ***".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.**_ **_ **_".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5._** _** ___".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.*** *** ___".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("5.**_ **_ ___".replaceAll("\\s", "")));

        Assert.assertTrue(rts.contains("1.___ ___ _**".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.___ ___ ***".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.___ ___ **_".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.___ _** _**".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.___ *** ***".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.___ **_ **_".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1._** _** ___".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.*** *** ___".replaceAll("\\s", "")));
        Assert.assertTrue(rts.contains("1.**_ **_ ___".replaceAll("\\s", "")));
}

}
