package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.ActivityManager;
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
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by axel_ on 2017-03-31.
 */

public class IndexActivity extends AppCompatActivity {
    public String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        /* Check if the application has a Shared Preferences file containing a username already */
        if(fileExists()){
            goToLobby();
        }

        /* Defining username */
        final Button button = (Button) findViewById(R.id.btn_submitUsername);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameInput = (EditText) findViewById(R.id.txt_username);
                createUsernameFile(usernameInput.getText().toString());
                username = usernameInput.getText().toString();
                goToLobby();
            }
        });
    }

    /* Checks if username file already exists */
    protected boolean fileExists(){
        String sharePrefName = "usernameFile.xml";
        File f = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/" + sharePrefName);
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
        PlayerInfo playerInfo = new PlayerInfo(username, deviceAddress);

        /* Start LobbyActivity*/
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(IntentType.MY_PLAYER_INFO, playerInfo);

        startActivity(intent);
        finish();
    }


}
