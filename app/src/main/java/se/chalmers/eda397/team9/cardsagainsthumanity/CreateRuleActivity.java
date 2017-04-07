package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CreateRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rule);
    }

    public void clickNext(View view) {
        //just for testing
        setContentView(R.layout.activity_join_game);
    }

    public void clickJoin(View view) {
        //just for testing
        setContentView(R.layout.activity_player_list);
    }

    public void clickTableRule(View view) {
        //just for testing
        setContentView(R.layout.activity_create_rule);
    }
}
