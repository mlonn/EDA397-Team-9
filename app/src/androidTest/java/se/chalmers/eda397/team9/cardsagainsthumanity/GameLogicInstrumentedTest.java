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
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by axel_ on 2017-04-27.
 */

@RunWith(JUnit4.class)
public class GameLogicInstrumentedTest {

    Game game;
    ArrayList<PlayerInfo> playerList;
    PlayerInfo king = new PlayerInfo("king");
    PlayerInfo p1 = new PlayerInfo("player1","player1","0","0");
    PlayerInfo p2 = new PlayerInfo("player2","player2","0","0");
    PlayerInfo p3 = new PlayerInfo("player3","player3","0","0");
    PlayerInfo p4 = new PlayerInfo("player4","player4","0","0");
    PlayerInfo p5 = new PlayerInfo("player5","player5","0","0");
    ArrayList<String> cardExpansionsNames;
    ArrayList<CardExpansion> cardExpansions;
    ArrayList<WhiteCard> whiteCards;
    boolean kingExists;
    Random random;
    BlackCard blackCard;

    @Before
    public void setUpInitGame(){
        whiteCards = new ArrayList<>();
        playerList = new ArrayList<>();
        //
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);
        playerList.add(p4);
        playerList.add(p5);
        playerList.add(king);
        cardExpansions = CardHandler.getExpansions(InstrumentationRegistry.getTargetContext());
        /*
        When Creating new Instance from Game -->
        Calling this method "createDummySelections" which will create Dummy 5 players and assign WhiteCards to them
        * */
        game = new Game(playerList, cardExpansionsNames, InstrumentationRegistry.getTargetContext());
        boolean kingExists = false;
    }

    @Test
    public void testInitGame(){
        for (PlayerInfo p:playerList) {
            assertEquals(10,p.getWhiteCards().size());
        }
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
        for (PlayerInfo p : playerList) {
            if (p.isKing()) noKing++;
        }
        assertEquals(1, noKing);

        // #3 Test Case:  King has Zero White Cards
        // By default / Correct Case
        assertEquals(10,game.getKing().getWhiteCards().size());
        // #4 Test Case: All players should submit white cards
        for (PlayerInfo p : playerList) {
            for (int k = 0; k < game.getBlackCard().getPick(); k++) {
                if(!p.isKing()){
                    p.submitSelection();
                }
            }
        }
        game.update();
        assertTrue(game.hasAllPlayersSubmitted());
        // #5 Test Case: Check Player Score by using givePoint Method
        // Round 1
        game.getPlayerByUUID("player1").givePoint();
        assertEquals("The score for Player1 should be 1",1,game.getPlayerByUUID("player1").getScore());
        // #6 Test Case: We have one winner per  Round Game - In this case, the winner is player0
        Submission winnerSubmission = game.getPlayerByUUID("player1").getSubmission();
        game.getPlayerByUUID("player1").setWinner(winnerSubmission);
        int noWinner = 0;
        for (PlayerInfo p : playerList) {
            if(p.getWinner()!=null) noWinner++;
        }
//        assertEquals(1, noWinner);
        // #7 Test Case: Creating many game rounds and Checking the score
        // Round 2
        game = new Game(playerList, cardExpansionsNames,InstrumentationRegistry.getTargetContext());
        game.getPlayerByUUID("player1").givePoint();
        Submission winnerSubmission1 = game.getPlayerByUUID("player1").getSubmission();
        game.getPlayerByUUID("player1").setWinner(winnerSubmission1);
        // Round 3
        game = new Game(playerList, cardExpansionsNames, InstrumentationRegistry.getTargetContext());
        game.getPlayerByUUID("player2").givePoint();
        Submission winnerSubmission2 = game.getPlayerByUUID("player2").getSubmission();
        game.getPlayerByUUID("player2").setWinner(winnerSubmission2);
        // creating  5 rounds
        for (int j=1;j<6;j++){
            game = new Game(playerList, cardExpansionsNames, InstrumentationRegistry.getTargetContext());
            game.getPlayerByUUID("player"+j).givePoint();
            Submission winnerSubmission$j = game.getPlayerByUUID("player"+j).getSubmission();
            game.getPlayerByUUID("player"+j).setWinner(winnerSubmission2);
        }
        // The expected Score -- Player1 = 3 ,Player2 = 2, Player3 = 1,Player4 = 1,Player5 = 1
        assertEquals(3,game.getPlayerByUUID("player1").getScore());
        assertEquals(2,game.getPlayerByUUID("player2").getScore());
        assertEquals(1,game.getPlayerByUUID("player3").getScore());
        assertEquals(1,game.getPlayerByUUID("player4").getScore());
        assertEquals(1,game.getPlayerByUUID("player5").getScore());
    }

    @After
    public void tearDownPlayGame(){
        for (PlayerInfo p: playerList){
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
