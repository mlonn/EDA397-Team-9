package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;

/**
 * Created by axel_ on 2017-03-31.
 */

public class WhiteCard implements Serializable {
    private String word;
    public WhiteCard(String word) {
        this.word = word;
    }
    //fetch random new white card
    //Invoked from Player
}
