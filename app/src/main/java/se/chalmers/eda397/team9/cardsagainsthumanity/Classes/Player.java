package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player implements Serializable {

    public String username = "";
    public int currentScore = 0;
    public boolean isKing;
    private List<WhiteCard> whiteCards = new ArrayList<>();
    //Do we need this?
    private String ip_adress = "";

    public Player(String username){
        this.username = username;
    }

    public WhiteCard playCard() {

        //When played a card get a new one-> always 10 cards on the hand
        //WhiteCard whiteCard = new WhiteCard();
        //whiteCards.add(whiteCard.getNewWhiteCard());
        //return new WhiteCard();
        return null;

    }

    public String getUsername(){
        return username;
    }

    //Returns the username which is stored in SharedPreferences
    public String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        return prefs.getString("name", null);
    }
}
