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
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo
        ;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by axel_ on 2017-04-27.
 */

@RunWith(JUnit4.class)
public class GameLogicInstrumentedTest {

    Game game;
    PlayerInfo player1;
    PlayerInfo player2;
    PlayerInfo player3;
    PlayerInfo player4;
    PlayerInfo player5;
    PlayerInfo player6;
    PlayerInfo player7;
    PlayerInfo player8;
    PlayerInfo player9;
    PlayerInfo player10;
    PlayerInfo player11;
    PlayerInfo player12;
    PlayerInfo player13;
    PlayerInfo player14;
    PlayerInfo player15;
    PlayerInfo player16;
    PlayerInfo player17;
    PlayerInfo player18;
    PlayerInfo player19;
    PlayerInfo player20;
    ArrayList<PlayerInfo> playerList;
    ArrayList<CardExpansion> cardExpansions;
    ArrayList<WhiteCard> whiteCards;
    boolean kingExists;

    Random random;
    BlackCard blackCard;

    @Before
    public void setUpInitGame(){
        whiteCards = new ArrayList<>();
        playerList = new ArrayList<>();
        player1 = new PlayerInfo("p1");
        player2 = new PlayerInfo("p2");
        player3 = new PlayerInfo("p3");
        player4 = new PlayerInfo("p4");
        player5 = new PlayerInfo("p5");
        player6 = new PlayerInfo("p6");
        player7 = new PlayerInfo("p7");
        player8 = new PlayerInfo("p8");
        player9 = new PlayerInfo("p9");
        player10 = new PlayerInfo("p10");
        player11 = new PlayerInfo("p11");
        player12 = new PlayerInfo("p12");
        player13 = new PlayerInfo("p13");
        player14 = new PlayerInfo("p14");
        player15 = new PlayerInfo("p15");
        player16 = new PlayerInfo("p16");
        player17 = new PlayerInfo("p17");
        player18 = new PlayerInfo("p18");
        player19 = new PlayerInfo("p19");
        player20 = new PlayerInfo("p20");
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
        for (PlayerInfo p:playerList) {
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
        for (PlayerInfo p: playerList) {
            if(!p.isKing()){
                //If dummy players exists and are added to the playerlist outside this class, reset their selected cards
                p.resetSubmission();
                while(p.getSelectedCards().size() != 0){
                    p.removeCardFromSelected(p.getSelectedCards().get(0));
                }

                ArrayList<WhiteCard> playerWhiteCard;
                playerWhiteCard = p.getWhiteCards();
                WhiteCard w = playerWhiteCard.get(random.nextInt(10));;
                List<WhiteCard> whiteCardsToSubmit = new ArrayList<>();

                for(int i = 0; i < 1; i++){
                    pickBlackCard();
                    assertTrue(blackCard.getPick() > 0 && blackCard.getPick() < 4);

                    while(p.getSelectedCards().size() < blackCard.getPick()){
                        w = playerWhiteCard.get(random.nextInt(10));
                        p.addCardToSelected(w);
                        whiteCardsToSubmit.add(w);
                    }

                    p.submitSelection();

                    assertEquals(p.getSubmission().getWhiteCards().size(), blackCard.getPick());

                    Submission s = new Submission(p, whiteCardsToSubmit);
                    int expected  = s.getWhiteCards().size();
                    int actual = p.getSubmission().getWhiteCards().size();

                    assertEquals(s.getPlayer(), p.getSubmission().getPlayer());
                    assertEquals(s.getWhiteCards().size(), p.getSubmission().getWhiteCards().size());

                    whiteCardsToSubmit.remove(w);
                    p.removeCardFromSelected(w);
                }
            }
        }
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
