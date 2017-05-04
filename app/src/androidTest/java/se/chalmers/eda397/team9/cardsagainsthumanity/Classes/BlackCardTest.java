package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TOSHIBA on 04/05/2017.
 */
public class BlackCardTest {
    BlackCard blackCard1;
    BlackCard blackCard2;
    @Before
    public void setUp() throws Exception {
        blackCard1  = new BlackCard("BlackCards1",3);
        blackCard2  = new BlackCard("BlackCards2",5);
    }

    @Test
    public void checkNumberOfPicks(){
        // Correct Case
        assertEquals("Number of picks between 1-3",blackCard1.getPick()>0&&blackCard1.getPick()<4,true);
        // Incorrect case
        assertEquals("Number of picks between 1-3",blackCard2.getPick()>0&&blackCard2.getPick()<4,true);
    }

    @After
    public void tearDown() throws Exception {
        blackCard1 = null;
        blackCard2 = null;
    }

}