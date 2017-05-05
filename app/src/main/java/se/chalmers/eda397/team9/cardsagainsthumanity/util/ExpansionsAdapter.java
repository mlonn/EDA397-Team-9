package se.chalmers.eda397.team9.cardsagainsthumanity.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.expansion_row, null);
            holder = new Holder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.expansionCheckBox);
            holder.expansionName = (TextView) view.findViewById(R.id.expansionName);
            holder.nbrBlackCards = (TextView) view.findViewById(R.id.no_blackCards);
            holder.nbrWhiteCards = (TextView) view.findViewById(R.id.no_whiteCards);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);
        }
        holder.checkBox.setFocusable(false);
        holder.checkBox.setChecked(expansions.get(position).isSelected());
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expansions.get(position).setSelection(true);
                } else {
                    expansions.get(position).setSelection(false);
                }
            }
        });
        holder.checkBox.setText(expansions.get(position).getName());
        holder.nbrBlackCards.setText(String.valueOf(expansions.get(position).getBlackCards().size()));
        holder.nbrWhiteCards.setText(String.valueOf(expansions.get(position).getWhiteCards().size()));

        return view;
    }
    static class Holder {
        TextView expansionName;
        TextView nbrBlackCards;
        TextView nbrWhiteCards;
        CheckBox checkBox;
    }
}

