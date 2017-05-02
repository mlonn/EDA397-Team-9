package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikae on 2017-04-24.
 */

public class Submission implements Serializable{
    private Player player;
    private List<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private boolean selected;
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

    public void setSelection(boolean selection) {
        this.selected = selection;
    }
    public boolean isSelected() {
        return selected;
    }
}
