package se.chalmers.eda397.team9.cardsagainsthumanity.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;

/**
 * Created by Mikae on 2017-04-20.
 */

public class ExpansionsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CardExpansion> expansions;
    private static LayoutInflater inflater = null;
    public ExpansionsAdapter(Context context, ArrayList<CardExpansion> expansions){
        this.context = context;
        this.expansions = expansions;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return expansions.size();
    }

    @Override
    public Object getItem(int position) {
        return expansions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println(position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.expansion_row, null);
        }
            TextView expansionTitle = (TextView) view.findViewById(R.id.expansionName);
            expansionTitle.setText(expansions.get(position).getName());

            TextView noBlackCards = (TextView) view.findViewById(R.id.no_blackCards);
            noBlackCards.setText(String.valueOf(expansions.get(position).getNoBlackCards()));

            TextView noWhiteCards = (TextView) view.findViewById(R.id.no_whiteCards);
            noWhiteCards.setText(String.valueOf(expansions.get(position).getNoWhiteCards()));

        return view;
    }
}
