package se.chalmers.eda397.team9.cardsagainsthumanity.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
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
            cardExpansionsList.add(new CardExpansion(data, data.getJSONObject(order.getString(i))));
        }
        return cardExpansionsList;
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
