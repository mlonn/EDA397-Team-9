package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel_ on 2017-03-31.
 */

public class Player implements Serializable {

    private String username;
    private int score;
    private boolean isKing;
    private ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private ArrayList<WhiteCard> selectedCards = new ArrayList<WhiteCard>();
    private Submission submission;
    private List<Submission> submissions;

    public Player(String username) {
        this.username = username;
        score = 0;
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

    public ArrayList<WhiteCard> getWhiteCards() {
        return whiteCards;
    }

    public void addWhiteCard(WhiteCard whiteCard) {
        whiteCards.add(whiteCard);
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

    public ArrayList<WhiteCard> getSelectedCards() {
        return selectedCards;
    }
}
