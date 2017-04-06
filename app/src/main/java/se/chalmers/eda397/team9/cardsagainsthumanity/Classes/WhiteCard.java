package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.io.Serializable;

/**
 * Created by axel_ on 2017-03-31.
 */

public class WhiteCard implements Serializable {
    public WhiteCard(){
        sentence = this.sentence;
    }

    public String sentence = "";

    //fetch random new white card
    //Invoked from Player
    public WhiteCard getNewWhiteCard()  {

        return new WhiteCard();
    }


}
