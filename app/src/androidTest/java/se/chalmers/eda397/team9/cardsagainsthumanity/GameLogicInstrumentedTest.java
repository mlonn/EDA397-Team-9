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
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;
    Player player6;
    Player player7;
    Player player8;
    Player player9;
    Player player10;
    Player player11;
    Player player12;
    Player player13;
    Player player14;
    Player player15;
    Player player16;
    Player player17;
    Player player18;
    Player player19;
    Player player20;
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
        player1 = new Player("p1");
        player2 = new Player("p2");
        player3 = new Player("p3");
        player4 = new Player("p4");
        player5 = new Player("p5");
        player6 = new Player("p6");
        player7 = new Player("p7");
        player8 = new Player("p8");
        player9 = new Player("p9");
        player10 = new Player("p10");
        player11 = new Player("p11");
        player12 = new Player("p12");
        player13 = new Player("p13");
        player14 = new Player("p14");
        player15 = new Player("p15");
        player16 = new Player("p16");
        player17 = new Player("p17");
        player18 = new Player("p18");
        player19 = new Player("p19");
        player20 = new Player("p20");
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        playerList.add(player5);
        playerList.add(player6);
        playerList.add(player7);
        playerList.add(player8);
        playerList.add(player9);
        playerList.add(player10);
        playerList.add(player11);
        playerList.add(player12);
        playerList.add(player13);
        playerList.add(player14);
        playerList.add(player15);
        playerList.add(player16);
        playerList.add(player17);
        playerList.add(player18);
        playerList.add(player19);
        playerList.add(player20);
        cardExpansions = CardHandler.getExpansions(InstrumentationRegistry.getTargetContext());
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
    public void testPlayGame(){
       game.getWinner();
    }
//
//    @Test
//    public void testPlayGame(){
//        for (Player p: playerList) {
//            if (!p.isKing()) {
//                //If dummy players exists and are added to the playerlist outside this class, reset their selected cards
//                p.resetSubmission();
//                while (p.getSelectedCards().size() != 0) {
//                    p.removeCardFromSelected(p.getSelectedCards().get(0));
//                }
//
//                ArrayList<WhiteCard> playerWhiteCard;
//                playerWhiteCard = p.getWhiteCards();
//                WhiteCard w = playerWhiteCard.get(random.nextInt(10));
//                ;
//                List<WhiteCard> whiteCardsToSubmit = new ArrayList<>();
//
//                // In this stage; we have 10 player with 10 cards for each player
//                for (int i = 0; i < 10; i++) {
//                    // Select 10 random whiteCards for each player.
//                    w = playerWhiteCard.get(random.nextInt(10));
//                    p.addCardToSelected(w);
//                    whiteCardsToSubmit.add(w);
//                    p.submitSelection();
//                    Submission s = new Submission(p, whiteCardsToSubmit);
//                    assertEquals(s.getPlayer(), p.getSubmission().getPlayer());
//                }
//            }
//            // After submission is completed, King can decide the winner!
//            // Player1 has 1 point for this round and Player1 is the winner & and Other players have Zero score.
//            Player king = game.getKing();
//            ArrayList<Submission> sub = (ArrayList<Submission>) king.getSubmissions();
//            king.setWinner(sub.get(19));
//            game.endTurn();
//            game.getWinner().getPlayer().givePoint();
//            for (Player p1 : playerList) {
//                if (!p1.isKing() && p1 != player1) {
//                    p1.setScore(0);
//                }
//            }
//            for (int i = 0; i < 10; i++) {
//                game = new Game(playerList, cardExpansions);
//            }
//        }
//    }

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
