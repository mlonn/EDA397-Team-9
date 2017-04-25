package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikae on 2017-04-24.
 */

public class Submission {
    private Player player;
    private List<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    public Submission (Player player, List<WhiteCard> whiteCards) {
        this.player = player;
        this.whiteCards = whiteCards;
    }

    public Player getPlayer() {
        return player;
    }

    public List<WhiteCard> getWhiteCards() {
        return whiteCards;
    }
}
