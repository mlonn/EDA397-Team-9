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

/**
 * Created by axel_ on 2017-03-31.
 */

public class IndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        final Button button = (Button) findViewById(R.id.btn_submitUsername);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Save the submitted username
                EditText usernameInput = (EditText) findViewById(R.id.txt_username);
                Player player = new Player(usernameInput.toString());
                SharedPreferences.Editor editor = getSharedPreferences("usernameFile", MODE_PRIVATE).edit();
                editor.putString("name", usernameInput.getText().toString());
                editor.commit();
                gotoCreateTable(v);
            }
        });

    }

    private void gotoCreateTable(View view) {
        Intent intent = new Intent(this, CreateTableActivity.class);
        startActivity(intent);
    }


}