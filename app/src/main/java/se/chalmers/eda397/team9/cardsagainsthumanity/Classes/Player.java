package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player implements Serializable {

    public String username = "";
    public int currentScore = 0;
    public boolean isKing;
    private ArrayList<WhiteCard> whiteCards = new ArrayList<>();
    private String ip_adress = "";

    public Player(String username) {
        this.username = username;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public WhiteCard playCard() {
        return null;
    }

    public String getUsername() {
        return username;
    }

    //Returns the username which is stored in SharedPreferences
    public String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        return prefs.getString("name", null);
    }

    public ArrayList<WhiteCard> getWhiteCards() {
        return whiteCards;
    }

    public void addWhiteCard(WhiteCard whiteCard) {
        whiteCards.add(whiteCard);
    }
}
