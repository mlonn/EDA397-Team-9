package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikae on 2017-04-08.
 */

public class CardExpansion {
    private List<BlackCard> blackCardList;
    private List<WhiteCard> whiteCardList;
    private String name;
    private boolean selected;

    public CardExpansion(JSONObject data, JSONObject expansion) {
        blackCardList = new ArrayList<>();
        whiteCardList = new ArrayList<>();
        selected = false;
        try {
            name = expansion.getString("name");
            JSONArray blackCards = data.getJSONArray("blackCards");
            JSONArray whiteCards = data.getJSONArray("whiteCards");
            JSONArray black = expansion.getJSONArray("black");
            JSONArray white = expansion.getJSONArray("white");
            for(int i = 0; i < black.length(); i++) {
                JSONObject b = blackCards.getJSONObject(Integer.parseInt(black.get(i).toString()));
                blackCardList.add(new BlackCard(b.getString("text"), b.getInt("pick")));
            }
            for(int i = 0; i < white.length(); i++) {
                whiteCardList.add(new WhiteCard("hej"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }


    public int getNoBlackCards() {
        return blackCardList.size();
    }
    public int getNoWhiteCards() {
        return whiteCardList.size();
    }

    public boolean getSelection(){
        return selected;
    }

    public void setSelection(boolean s) {
        selected = s;
    }
}
