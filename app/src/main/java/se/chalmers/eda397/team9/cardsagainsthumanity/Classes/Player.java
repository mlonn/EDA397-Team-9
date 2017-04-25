package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player implements Serializable {

    public String username = "";
    public int score = 0;
    public boolean isKing;
    private BlackCard blackCard;
    private ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private ArrayList<WhiteCard> selectedCards = new ArrayList<WhiteCard>();
    private Submission submission;
    private String ip_adress = "";
    private List<Submission> submissions;

    public Player(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public void addCardToSelected(WhiteCard whiteCard) {
        selectedCards.add(whiteCard);
    }
    public void removeCardFromSelected(WhiteCard whiteCard) {
        selectedCards.remove(whiteCard);
    }
    public void submitSelection() {
        submission = new Submission(this, selectedCards);
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

    public void setBlackCard(BlackCard blackCard) {
        this.blackCard = blackCard;
    }

    public void resetSubmissions() {
        submissions = null;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public Submission getSubmission() {
        return submission;
    }
}
