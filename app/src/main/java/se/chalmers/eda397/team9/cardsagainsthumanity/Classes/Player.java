package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player {

    public String username = "";
    public int currentScore = 0;
    public boolean isKing;
    private List<WhiteCard> whiteCards = new ArrayList<>();
    //Do we need this?
    private String ip_adress = "";

    public Player(String username) {
        this.username = username;
        this.ip_adress = ip_adress = "";
        this.isKing = isKing;
    }

    public WhiteCard playCard() {

        //When played a card get a new one-> always 10 cards on the hand
        WhiteCard whiteCard = new WhiteCard();
        whiteCards.add(whiteCard.getNewWhiteCard());
        return new WhiteCard();

    }

    public void setUsername() {

        //TODO
        //Store username in db or similar

    }


}

