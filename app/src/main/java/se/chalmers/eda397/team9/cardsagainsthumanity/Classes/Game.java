package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

/**
 * Created by Mikae on 2017-04-21.
 */

public class Game implements Serializable {
    private ArrayList<String> expansionNames;
    private List<PlayerInfo> players;
    private PlayerInfo king;
    private Submission winningSubmission;
    private BlackCard blackCard;
    private ArrayList<CardExpansion> cardExpansions;
    private Random r;
    private boolean endTurn;

    public Game(ArrayList<PlayerInfo> players, ArrayList<String> cardExpansions, Context ctx) {
        r = new Random();
        this.players = players;
        this.expansionNames = cardExpansions;
        king = setKing();
        pickBlackCard(ctx);
        distributeWhiteCards(expansionNames, ctx);
    }

    public Game(ArrayList<PlayerInfo> players, ArrayList<CardExpansion> cardExpansions, PlayerInfo king, BlackCard blackCard) {
        this.players = players;
        this.cardExpansions = cardExpansions;
        this.king = king;
        this.blackCard = blackCard;
    }
    public void initExpansions(ArrayList<String> expansionNames, Context ctx) {
        cardExpansions =  CardHandler.getExpansions(expansionNames, ctx);
    }
    //Call this method with what ever update frequency you want
    public void update() {
        giveCardsToKing();
    }

    public boolean endTurn(Context ctx){
        if (king.getWinner() != null){
            winningSubmission = king.getWinner();
            PlayerInfo winner = winningSubmission.getPlayer();
            winner.givePoint();
            king = setKing();
            pickBlackCard(ctx);
            resetPlayers();
            distributeWhiteCards(expansionNames, ctx);
            return true;
        }
        return false;
    }

    private void resetPlayers() {
        for (PlayerInfo p : players) {
            p.reset();
        }
    }


    //gives each player 10 cards from selected expansion
    private void distributeWhiteCards(ArrayList<String> expansionNames, Context ctx) {
        ArrayList<CardExpansion> cardExpansions= CardHandler.getExpansions(expansionNames, ctx);;
        for (PlayerInfo p : players) {
            while (p.getWhiteCards().size() < 10) {
                CardExpansion exp = cardExpansions.get(r.nextInt(cardExpansions.size()));
                WhiteCard whiteCard = exp.getWhiteCards().get(r.nextInt(exp.getWhiteCards().size()));
                p.addWhiteCard(whiteCard);
            }
        }
    }

    //Selects a random black card from selected expansions
    private void pickBlackCard(Context ctx) {
        //Remove temporarily all expansions which don't possess any black cards from being picked
        ArrayList<CardExpansion> cardExpansions= CardHandler.getExpansions(expansionNames, ctx);
        List<CardExpansion> tempRemovedExp = new ArrayList<>();

        for (CardExpansion expansion : cardExpansions) {
            if (expansion.getBlackCards().size() == 0) {
                tempRemovedExp.add(expansion);
            }
        }
        cardExpansions.removeAll(tempRemovedExp);

        //Pick black card
        CardExpansion exp = cardExpansions.get(r.nextInt(cardExpansions.size()));
        blackCard = exp.getBlackCards().get(r.nextInt(exp.getBlackCards().size()));

        //Add expansions back again to list of picked card expansions
        for (CardExpansion expansion : tempRemovedExp) {
            cardExpansions.add(expansion);
        }
    }

    public void setKing(PlayerInfo player) {
        players.remove(player);
        PlayerInfo king = setKing();
        players.add(player);
    }

    public PlayerInfo setKing() {
        //Set all players to not being king
        for (PlayerInfo p : players) {
            p.setKing(false);
        }
        //Assign new king
        int kingNumber = r.nextInt(players.size());
        players.get(kingNumber).setKing(true);
        return players.get(kingNumber);
    }

    //collects all submitted white cards and gives them to the king
    private void giveCardsToKing() {
        List<Submission> sub = new ArrayList<Submission>();
        king.resetSubmissions();
        for (PlayerInfo p : players) {


            if (!p.equals(king)) {
                if (!p.equals(king) && p.getSubmission() != null) {
                    sub.add(p.getSubmission());
                }
            }
        }
        king.setSubmissions(sub);
    }

    public Boolean hasAllPlayersSubmitted() {
        return (king.getSubmissions().size() == (players.size() - 1) * blackCard.getPick());
    }

    public PlayerInfo getPlayerByUUID(String UUID) {
        for (PlayerInfo p : players) {
            if (p.getUUID().equals(UUID)) {
                return p;
            }
        }
        return null;
    }

    public BlackCard getBlackCard() {
        return blackCard;
    }

    public boolean turnEnded() {
        return endTurn;
    }

    public Submission getWinner() {
        return winningSubmission;
    }

    public List<PlayerInfo> getPlayerList() {
        return players;
    }

    public PlayerInfo getKing() {
        return king;
    }

    public ArrayList<String> getExpansionNames() {
        return expansionNames;
    }
    public String updateBlackCardText(List<WhiteCard> whiteCards) {
        String[] blackText = getBlackCard().getText().split("_");
        StringBuilder sb = new StringBuilder();
        if (blackText.length>1) {
            for (int j = 0; j < blackText.length; j++) {
                sb.append(blackText[j]);
                if (j < whiteCards.size()) {
                    sb.append(whiteCards.get(j).getWord());
                } else if (j < blackText.length-1) {
                    sb.append("_");
                }
            }
        }
        return sb.toString();
    }
}
