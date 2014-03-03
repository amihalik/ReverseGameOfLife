package amihalik.conway;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import amihalik.conway.TileSymmetry;

import com.google.common.collect.Sets;

public class TileSymmetryTest {
    @Test
    public void increaseTest() throws Exception {
        Set<String> expected = Sets.newHashSet();
        expected.add(TileSymmetry.arrayToString(a));
        expected.add(TileSymmetry.arrayToString(b));
        expected.add(TileSymmetry.arrayToString(c));
        expected.add(TileSymmetry.arrayToString(d));
        expected.add(TileSymmetry.arrayToString(e));
        expected.add(TileSymmetry.arrayToString(f));
        expected.add(TileSymmetry.arrayToString(g));
        expected.add(TileSymmetry.arrayToString(h));

        Assert.assertEquals(8, expected.size());
        Assert.assertTrue(expected.contains("123456789"));
        Assert.assertTrue(expected.contains("147258369"));
        
        Assert.assertArrayEquals(a, TileSymmetry.strToArray("123456789"));
        
        Assert.assertEquals(expected, TileSymmetry.calcSymmetricTiles("123456789"));
    }

    private static final char[][] a = {//
    //
            { '1', '2', '3' },//
            { '4', '5', '6' },//
            { '7', '8', '9' },//
    };
    private static final char[][] b = {//
    //
            { '7', '4', '1' },//
            { '8', '5', '2' },//
            { '9', '6', '3' },//
    };

    private static final char[][] c = {//
    //
            { '9', '8', '7' },//
            { '6', '5', '4' },//
            { '3', '2', '1' },//
    };
    private static final char[][] d = {//
    //
            { '3', '6', '9' },//
            { '2', '5', '8' },//
            { '1', '4', '7' },//
    };

    private static final char[][] e = {//
    //
            { '7', '8', '9' },//
            { '4', '5', '6' },//
            { '1', '2', '3' },//
    };
    private static final char[][] f = {//
    //
            { '1', '4', '7' },//
            { '2', '5', '8' },//
            { '3', '6', '9' },//
    };
    private static final char[][] g = {//
    //
            { '3', '2', '1' },//
            { '6', '5', '4' },//
            { '9', '8', '7' },//
    };
    private static final char[][] h = {//
    //
            { '9', '6', '3' },//
            { '8', '5', '2' },//
            { '7', '4', '1' },//
    };

}
