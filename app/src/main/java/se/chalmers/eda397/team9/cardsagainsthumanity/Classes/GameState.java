package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by Mikae on 2017-05-11.
 */

public class GameState implements Serializable{
    private PlayerInfo newKing;
    private Submission winner;
    private BlackCard blackCard;

    public GameState(PlayerInfo newKing, Submission winner, BlackCard blackCard){
        this.newKing = newKing;
        this.winner = winner;
        this.blackCard = blackCard;
    }

    public PlayerInfo getNewKing() {
        return newKing;
    }

    public Submission getWinner() {
        return winner;
    }

    public BlackCard getBlackCard() {
        return blackCard;
    }
}
