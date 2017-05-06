package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TOSHIBA on 06/05/2017.
 */
public class PlayerTest {
    Player player1;
    Player player2;
    @Before
    public void setUp() throws Exception {
        player1  =  new Player("player_1");
        player2 =  new Player("Player_2");
    }

    @Test
    public void checkPlayerInitial(){
        assertNotEquals(player1.getUsername(),"");
        assertEquals(player1.getScore(),0);
    }

    @Test
    public void checkSetScore(){
        // correct Test Case
        player1.setScore(0);
        // Incorrect Test case
        player2.setScore(-1);
        assertTrue(player1.getScore()>= 0);
        assertTrue(player2.getScore()>= 0);
    }

    @After
    public void tearDown() throws Exception {
        player1 = null;
        player2 = null;
    }

}