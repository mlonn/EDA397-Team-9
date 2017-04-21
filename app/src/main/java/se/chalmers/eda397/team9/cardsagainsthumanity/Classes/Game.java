package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mikae on 2017-04-21.
 */

public class Game {
    private ArrayList<Player> players;
    private Player king;
    private BlackCard blackCard;
    private ArrayList<CardExpansion> cardExpansions;
    private Random r;

    public Game(ArrayList<Player> players, ArrayList<CardExpansion> cardExpansions) {
        r = new Random();
        this.players = players;
        this.cardExpansions = cardExpansions;
        king = setKing();
        pickBlackCard();
        distributeWhiteCards();
    }

    private void distributeWhiteCards() {
        for (Player p : players) {
            if (!p.isKing()) {
                while(p.getWhiteCards().size() < 10) {
                    CardExpansion exp = cardExpansions.get(r.nextInt(cardExpansions.size()));
                    WhiteCard whiteCard = exp.getWhiteCards().get(r.nextInt(exp.getWhiteCards().size()));
                    p.addWhiteCard(whiteCard);
                }
            }
        }
    }

    private void pickBlackCard() {
        CardExpansion exp = cardExpansions.get(r.nextInt(cardExpansions.size()));
        blackCard = exp.getBlackCards().get(r.nextInt(exp.getBlackCards().size()));
    }

    private Player setKing() {
        for (Player p : players) {
            p.setKing(false);
        }
        int kingNumber = r.nextInt(players.size());
        players.get(kingNumber).setKing(true);
        return players.get(kingNumber);
    }
}
