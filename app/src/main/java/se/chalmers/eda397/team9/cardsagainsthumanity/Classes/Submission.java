package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by Mikae on 2017-04-24.
 */

public class Submission implements Serializable{
    private PlayerInfo player;
    private List<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private boolean selected;
    public Submission (PlayerInfo player, List<WhiteCard> whiteCards) {
        this.player = player;
        this.whiteCards = whiteCards;
    }

    public PlayerInfo getPlayer() {
        return player;
    }

    public List<WhiteCard> getWhiteCards() {
        return whiteCards;
    }
}
