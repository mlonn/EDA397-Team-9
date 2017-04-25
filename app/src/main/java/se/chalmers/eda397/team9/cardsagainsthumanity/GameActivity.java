package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by emy on 23/04/17.
 */

public class GameActivity extends Activity {

    String[] textWhiteCard = new String [10];
    String textBlackCard = "Black card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(550, 550);
            params.setMargins(1, 1, 1, 1);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.white_card));
            layout.addView(imageView);
        }


    }
    public void setText() {
        //Set text of white cards (later on also the black card)
        for (int i=0; i<10; i++){
            textWhiteCard[i]="White card " + i;
        }
    }
}
