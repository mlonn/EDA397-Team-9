package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        final Button button = (Button) findViewById(R.id.createTable_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Save the submitted tablename
                EditText usernameInput = (EditText) findViewById(R.id.tablename);
                Player player = new Player(usernameInput.toString());
                SharedPreferences.Editor editor = getSharedPreferences("tableNameFile", MODE_PRIVATE).edit();
                editor.putString("name", usernameInput.getText().toString());
                editor.commit();
                if(v.getId() == R.id.createTable_button)
                goToRule(v);
                else if(v.getId() == R.id.joinTable_button)
                    goToPlayerList(v);
            }
        });
    }

    public void goToRule(View view) {
        Intent intent = new Intent(this, CreateRuleActivity.class);
        startActivity(intent);
    }

    public void goToPlayerList(View view) {
        Intent intent = new Intent(this, CreatePlayerList.class);
        startActivity(intent);
    }
}
