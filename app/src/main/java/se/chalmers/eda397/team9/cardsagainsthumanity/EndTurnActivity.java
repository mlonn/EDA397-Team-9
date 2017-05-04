package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;

public class EndTurnActivity extends AppCompatActivity {
    private Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_turn);
        game = (Game) getIntent().getExtras().get(IntentType.THIS_GAME);
        String str = (String) getIntent().getExtras().get(IntentType.WINNING_STRING);
        TextView blackCardText = (TextView) findViewById(R.id.textviewWinningBlackCard);
        TextView winner = (TextView) findViewById(R.id.winnerTextView);
        blackCardText.setText(str);
        winner.setText(game.getWinner().getPlayer().getUsername());
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app Next activity
                Intent i = new Intent(EndTurnActivity.this, GameActivity.class);
                i.putExtra(IntentType.THIS_GAME, game);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 2000);

    }
}
