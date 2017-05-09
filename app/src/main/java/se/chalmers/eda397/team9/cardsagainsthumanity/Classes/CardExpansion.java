package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikae on 2017-04-08.
 */

public class CardExpansion implements Serializable{

    private List<BlackCard> blackCardList;
    private List<WhiteCard> whiteCardList;
    private String name;
    private boolean selected;

    public CardExpansion(String name, ArrayList blackCardList, ArrayList whiteCardList) {
        this.blackCardList = blackCardList;
        this.whiteCardList = whiteCardList;
        selected = false;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<BlackCard> getBlackCards() {
        return blackCardList;
    }

    public List<WhiteCard> getWhiteCards() {
        return whiteCardList;
    }

    //used to know if expansions will be used in game creation
    public void setSelection(boolean s) {
        selected = s;
    }
    public boolean isSelected(){
        return selected;
    }
}
