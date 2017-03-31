package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player {
    public Player(){
        username = this.username;
        currentScore= this.currentScore = 0;
        ip_adress = this.ip_adress = "";
        isKing = this.isKing;
        whiteCards = this.whiteCards;
    }

    public String username = "";
    public int currentScore = 0;
    public boolean isKing;

    private List<WhiteCard> whiteCards = new ArrayList<>();
    //Do we need this?
    private String ip_adress = "";

    public WhiteCard playCard(){

        //When played a card get a new one-> always 10 cards on the hand
        WhiteCard whiteCard = new WhiteCard();
        whiteCards.add(whiteCard.getNewWhiteCard());

        return new WhiteCard();
    }

    public void setUsername(String chosenUserName){
        username = chosenUserName;
        //TODO
        //Store username in db or similar

    }


}

