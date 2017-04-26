package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;


import java.io.Serializable;

public class BlackCard implements Serializable {
    private final String text;
    private final  int pick;

    public BlackCard(String text, int pick){
        this.text = text;
        this.pick = pick;
    }

    public int getPick() {
        return pick;
    }

    public String getText() {
        return text;
    }
}
