package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.BlackCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by axel_ on 2017-04-27.
 */

@RunWith(JUnit4.class)
public class GameLogicInstrumentedTest {

    Game game;
    ArrayList<Player> playerList;
    ArrayList<CardExpansion> cardExpansions;
    ArrayList<WhiteCard> whiteCards;
    boolean kingExists;
    Random random;
    BlackCard blackCard;

    @Before
    public void setUpInitGame(){
        whiteCards = new ArrayList<>();
        playerList = new ArrayList<>();
        playerList.add(new Player("test"));
        cardExpansions = CardHandler.getExpansions(InstrumentationRegistry.getTargetContext());
        /*
        When Creating new Instance from Game -->
        Calling this method "createDummySelections" which will create Dummy 5 players and assign WhiteCards to them
        * */
        game = new Game(playerList, cardExpansions);
        boolean kingExists = false;
    }

    @Test
    public void testInitGame(){
        for (Player p:playerList) {
            if(p.isKing()){
                kingExists = true;
                assertEquals(0, p.getWhiteCards().size());
            }
            else{
                assertEquals(10,p.getWhiteCards().size());
            }
        }
        assertEquals(kingExists, true);
    }

    @After
    public void tearDownInitGame(){
        game = null;
        cardExpansions = null;
        kingExists = false;
    }

    @Before
    public void setUpPlayGame(){
        random = new Random();
        cardExpansions = CardHandler.getExpansions(InstrumentationRegistry.getTargetContext());
        CardExpansion expansion = cardExpansions.get(random.nextInt(cardExpansions.size()));
        blackCard = expansion.getBlackCards().get(random.nextInt(expansion.getBlackCards().size()));
    }

    @Test
    public void testPlayGame() {
        // After initialization -
        // #1 Test Case: We have just one king and many players
        int noKing = 0;
        for (Player p : playerList) {
            if (p.isKing()) noKing++;
        }
        assertEquals(1, noKing);
        /* #2
        The king should have one Black card per round Or more!!
        How can I get the black cards for the king!
        * */
        // #3 Test Case:  King has Zero White Cards
        // By default / Correct Case
        assertEquals(0,game.getKing().getWhiteCards().size());
        // #4 Test Case: All players should submit white cards
        assertTrue(game.hasAllPlayersSubmitted());
        // #5 Test Case: We have one winner per  Round Game

        int noWinner = 0;
//        assertEquals(1, noWinner);
    }

    @After
    public void tearDownPlayGame(){
        for (Player p: playerList){
            p.resetSubmissions();
            blackCard = null;
        }
    }

    private void pickBlackCard(){
        //Remove temporarily all expansions which don't possess any black cards from being picked
        List<CardExpansion> tempRemovedExp = new ArrayList<>();
        for (CardExpansion expansion:cardExpansions)
        {
            if(expansion.getBlackCards().size() == 0){
                tempRemovedExp.add(expansion);
            }
        }
        cardExpansions.removeAll(tempRemovedExp);

        //Pick black card
        CardExpansion exp = cardExpansions.get(random.nextInt(cardExpansions.size()));
        blackCard = exp.getBlackCards().get(random.nextInt(exp.getBlackCards().size()));

        //Add expansions back again to list of picked card expansions
        for (CardExpansion expansion:tempRemovedExp){
            cardExpansions.add(expansion);
        }
    }
}
