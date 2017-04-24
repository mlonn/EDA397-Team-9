package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by axel_ on 2017-03-31.
 */

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        final Context context = this;
        final Player player = new Player("");

        //Check if the application has a Shared Preferences file containing a username already
        if(fileExists()){
            player.username = player.getUsername(context);
            Intent intent = new Intent(this, CreateTableActivity.class);
            startActivity(intent);
            finish();
        }

        final Button button = (Button) findViewById(R.id.btn_submitUsername);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameInput = (EditText) findViewById(R.id.txt_username);
                createUsernameFile(usernameInput.getText().toString());
                player.username = player.getUsername(context);
                gotoCreateTable(v);
            }
        });
    }
    protected boolean fileExists(){

        String sharePrefName = "usernameFile.xml";
        File f = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/"+ sharePrefName);
        if (f.exists())
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    protected void createUsernameFile(String username2Save){
        SharedPreferences.Editor editor = getSharedPreferences("usernameFile", MODE_PRIVATE).edit();
        editor.putString("name", username2Save);
        editor.commit();
    }

    private void gotoCreateTable(View view) {
        Intent intent = new Intent(this, CreateTableActivity.class);
        startActivity(intent);
    }

    //Main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeName:
                try{
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                }
                catch (Exception e){

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);

                return true;
            case R.id.changeTable:
                //Do something
                return true;
            case R.id.blackList:
                //Do something
                return true;
            case R.id.settings:
                //Do something
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
