package se.chalmers.eda397.team9.cardsagainsthumanity.util;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.BlackCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by Mikae on 2017-04-20.
 */

public class BlackCardAdapter extends BaseAdapter {
    private final BlackCard blackCard;
    private final PlayerInfo king;
    private Context context;
    private List<Submission> submissions;
    private static LayoutInflater inflater = null;
    private int selectedPosition = 0;
    public BlackCardAdapter(Context context, BlackCard blackCard, List<Submission> submissions, PlayerInfo king){
        this.context = context;
        this.submissions = (ArrayList<Submission>) submissions;
        this.blackCard = blackCard;
        this.king = king;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return submissions.size();
    }

    @Override
    public Object getItem(int position) {
        return submissions.get(position);
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
            view = inflater.inflate(R.layout.black_card, null);
            holder = new Holder();
            holder.cardImage = (ImageView) view.findViewById(R.id.blackCardImageView);
            holder.cardText = (TextView) view.findViewById(R.id.blackCardTextView);
            holder.cardRadioButton = (RadioButton) view.findViewById(R.id.blackCardRadioButton);
            king.setWinner(submissions.get(0));
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
            holder.cardRadioButton.setOnCheckedChangeListener(null);
        }
        holder.cardRadioButton.setFocusable(false);
        holder.cardRadioButton.setTag(position);
        holder.cardRadioButton.setChecked(selectedPosition == position);
        holder.cardRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = (Integer) v.getTag();
                king.setWinner(submissions.get(selectedPosition));
                notifyDataSetChanged();
            }
        });
        if (submissions != null) {
            holder.cardText.setText(Html.fromHtml(getBlackCardText(blackCard, submissions.get(position).getWhiteCards())));
        }
        return view;
    }

        private String getBlackCardText(BlackCard blackCard, List<WhiteCard> whiteCards) {
        String[] blackText = blackCard.getText().split("_");
        if (blackText.length>1) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < blackText.length; j++) {
                sb.append(blackText[j]);
                if (j < whiteCards.size()) {
                    sb.append(whiteCards.get(j).getWord());
                } else if (j < blackText.length-1) {
                    sb.append("_");
                }
            }
            return sb.toString();
        }
        return blackCard.getText() + "<br>" + whiteCards.get(0).getWord();
    }

    public void update(List<Submission> submissions) {
        this.submissions = submissions;
        notifyDataSetChanged();
    }

    private static class Holder {
        TextView cardText;
        ImageView cardImage;
        RadioButton cardRadioButton;
    }
}

