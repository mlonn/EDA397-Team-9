package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;

/**
 * Created by SAMSUNG on 2017-04-25.
 */

public class PlayerInfo implements Serializable{

    private String name;
    private String color;
    private String deviceAddress;
    private String UUID;
    private int score;
    private boolean isKing;
    private ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private ArrayList<WhiteCard> selectedCards = new ArrayList<WhiteCard>();
    private Submission submission;

    private Submission winner;
    private List<Submission> submissions;

    public PlayerInfo(String name){
        this.name = name;
        color = "#000000";
        deviceAddress = "Test_Address";
    }

    public String getUUID() {
        return UUID;
    }

    public PlayerInfo(String name, String deviceAddress){
        this.name = name;
        color = "#000000";
        this.UUID = deviceAddress;
        this.deviceAddress = deviceAddress;
    }

    public PlayerInfo(String name, String deviceAddress, String color){
        this.name = name;
        this.deviceAddress = deviceAddress;
        this.color = color;
    }

    public synchronized void setColor(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }

    public String getName(){
        return name;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }

    public synchronized void setDeviceAddress(String deviceAddress){
        this.deviceAddress = deviceAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlayerInfo))
            return false;
        PlayerInfo playerInfo = (PlayerInfo) obj;
        if(!deviceAddress.equals(playerInfo.getDeviceAddress()))
            return false;
        if(!name.equals(playerInfo.getName()))
            return false;
        if(!color.equals(playerInfo.getColor()))
            return false;
        return true;
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

    public void setKing() {
        isKing = true;
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


    public ArrayList<WhiteCard> getWhiteCards() {
        return whiteCards;
    }

    public void addWhiteCard(WhiteCard whiteCard) {
        whiteCards.add(whiteCard);
    }

    public void resetSubmission(){ submission = null; }

    public void resetSubmissions() {
        submissions = new ArrayList<Submission>();
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

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public Submission getWinner() {
        return winner;
    }

    public void setWinner(Submission winner) {
        this.winner = winner;
    }

    public void givePoint() {
        score++;
    }

    public void reset() {
        submission = null;
        winner = null;
        selectedCards = new ArrayList<WhiteCard>();
        resetSubmissions();
    }
}
