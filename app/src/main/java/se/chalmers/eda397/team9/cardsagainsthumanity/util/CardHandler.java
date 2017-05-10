package se.chalmers.eda397.team9.cardsagainsthumanity.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.BlackCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;

/**
 * Created by Mikae on 2017-04-08.
 */

public class CardHandler {

    public static ArrayList<CardExpansion> getExpansions(Context ctx) {
        try {
            JSONObject obj;
            obj = new JSONObject(getJsonString(ctx));
            return createExpansions(obj, obj.getJSONArray("order"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<CardExpansion>();
    }

    private static ArrayList<CardExpansion> createExpansions(JSONObject data, JSONArray order) throws JSONException {
        ArrayList<CardExpansion> cardExpansionsList = new ArrayList<>();
        for (int i = 0; i < order.length(); i++) {
            cardExpansionsList.add(createExpansion(data, data.getJSONObject(order.getString(i))));
        }
        return cardExpansionsList;
    }

    private static CardExpansion createExpansion(JSONObject data, JSONObject expansion) {
        ArrayList blackCardList = new ArrayList<>();
        ArrayList whiteCardList = new ArrayList<>();
        try {
            String name = expansion.getString("name");
            JSONArray blackCards = data.getJSONArray("blackCards");
            JSONArray whiteCards = data.getJSONArray("whiteCards");
            JSONArray black = expansion.getJSONArray("black");
            JSONArray white = expansion.getJSONArray("white");
            for (int i = 0; i < black.length(); i++) {
                JSONObject b = blackCards.getJSONObject(Integer.parseInt(black.get(i).toString()));
                blackCardList.add(new BlackCard(b.getString("text"), b.getInt("pick")));
            }
            for (int i = 0; i < white.length(); i++) {
                whiteCardList.add(new WhiteCard(whiteCards.getString(Integer.parseInt(white.getString(i)))));
            }
            return new CardExpansion(name, blackCardList, whiteCardList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getJsonString(Context ctx) {
        String json = null;
        try {

            InputStream is = ctx.getResources().openRawResource(R.raw.cards);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
