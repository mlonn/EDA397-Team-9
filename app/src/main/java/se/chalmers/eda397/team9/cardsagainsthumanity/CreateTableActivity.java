package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;



public class CreateTableActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtable);
        Intent intent = getIntent();
        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        Button joinTableButton = (Button) findViewById(R.id.joinTable_button);


        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateRuleActivity.class);
                startActivity(intent);
            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreatePlayerListActivity.class);
                startActivity(intent);
            }
        });
    }
}
