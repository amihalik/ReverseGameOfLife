package amihalik.conway;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import amihalik.conway.GameOfLife;
import amihalik.conway.ProvidedData;

public class GameOfLifeTest {

    @Test
    public void test() throws Exception {
        ProvidedData pd = new ProvidedData();
        Map<Integer, Integer> deltas = pd.getTrainDelta();
        Map<Integer, GameOfLife> startGames = pd.getTrainStart();
        Map<Integer, GameOfLife> endGames = pd.getTrainEnd();

        for (Integer key : startGames.keySet()) {
            int delta = deltas.get(key);
            GameOfLife start = startGames.get(key);
            GameOfLife endExpected = endGames.get(key);

            GameOfLife endActual = start.process(delta);

            // System.out.println("Start");
            // System.out.println(start.prettyPrint());
            //
            // System.out.println("Delta: " + delta);
            //
            // System.out.println("endExpected");
            // System.out.println(endExpected.prettyPrint());
            //
            // System.out.println("endActual");
            // System.out.println(endActual.prettyPrint());

            boolean isEqual = endActual.equals(endExpected);
            System.out.println(key + ": " + isEqual);
            Assert.assertTrue(isEqual);
        }
    }
    
    

}
