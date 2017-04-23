package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.os.Bundle;
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
        TextView[] t = new TextView[11];
        t[0] = (TextView) findViewById(R.id.firstCardText);
        t[1] = (TextView) findViewById(R.id.secondCardText);
        t[2] = (TextView) findViewById(R.id.thirdCardText);
        t[3] = (TextView) findViewById(R.id.fourthCardText);
        t[4] = (TextView) findViewById(R.id.fifthCardText);
        t[5] = (TextView) findViewById(R.id.sixthCardText);
        t[6] = (TextView) findViewById(R.id.sevenCardText);
        t[7] = (TextView) findViewById(R.id.eighthCardText);
        t[8] = (TextView) findViewById(R.id.ninthCardText);
        t[9] = (TextView) findViewById(R.id.tenthCardText);
        t[10] = (TextView) findViewById(R.id.blackCardText);

        t[10].setText(textBlackCard);
        setText();
        for (int i=0; i<10; i++){
            t[i].setText(textWhiteCard[i]);
        }

    }
    public void setText() {
        //Set text of white cards (later on also the black card)
        for (int i=0; i<10; i++){
            textWhiteCard[i]="White card " + i;
        }
    }
}
