package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by axel_ on 2017-03-31.
 */

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        /* Check if the application has a Shared Preferences file containing a username already */
//        if(fileExists()){
//            goToLobby();
//        }

        /* Defining username */
        final Button button = (Button) findViewById(R.id.btn_submitUsername);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameInput = (EditText) findViewById(R.id.txt_username);
                createUsernameFile(usernameInput.getText().toString());
                goToLobby();
            }
        });
    }

    /* Checks if username file already exists */
    protected boolean fileExists(){
        String sharePrefName = "usernameFile.xml";
        File f = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/"+ sharePrefName);
        if (f.exists())
            return true;
        else
            return false;
    }

    /* Creates a username file */
    protected void createUsernameFile(String username2Save){
        SharedPreferences.Editor editor = getSharedPreferences("usernameFile", MODE_PRIVATE).edit();
        editor.putString("name", username2Save);
        editor.commit();
    }

    /* Go to lobby activity */
    private void goToLobby() {
        /* Get device address */
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String deviceAddress = wifiManager.getConnectionInfo().getMacAddress();

        /* Get stored username */
        SharedPreferences prefs = getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        String username = prefs.getString("name", null);
        PlayerInfo playerInfo = new PlayerInfo(username);

        /* Start LobbyActivity*/
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("PLAYER_INFO", playerInfo);

        startActivity(intent);
        finish();
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
                } catch (Exception e){

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);

                return true;
            case R.id.changeTable:
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
