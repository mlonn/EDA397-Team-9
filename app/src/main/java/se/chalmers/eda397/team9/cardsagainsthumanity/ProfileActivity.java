package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;


/**
 * Created by mccol on 03/05/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private Menu menu;
    private boolean flag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        String username = prefs.getString("name", null);
        final TextView textViewToChange = (TextView) findViewById(R.id.textView11);
        textViewToChange.setText(username);
        Button createTableButton = (Button) findViewById(R.id.EditName);
        createTableButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                try {
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                } catch (Exception e) {


                }
                Intent intent = new Intent(view.getContext(), IndexActivity.class);
                                                     startActivity(intent);
            }
        });
    }



    //Main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        String username = prefs.getString("name", null);
        menu.findItem(R.id.profile).setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings:
                //Do something
                return true;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! I'm playing this wonderful game called King of Cards. Please download it you too from Play store so we can play together!";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "King of Cards");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
