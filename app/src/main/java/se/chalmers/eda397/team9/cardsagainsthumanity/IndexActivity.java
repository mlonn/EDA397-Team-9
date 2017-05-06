package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.UUID;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;

/**
 * Created by axel_ on 2017-03-31.
 */

public class IndexActivity extends AppCompatActivity {
    public String username;

    public static final String GAME_SETTINGS_FILE = "king_of_cards_settings";
    public static final String MULTICAST_IP_ADDRESS = "multicast_ip_address";
    public static final String P2P_PORT = "p2p_port";
    public static final String MULTICAST_PORT = "multicast_port";
    public static final String PLAYER_NAME = "player_name";
    public static final String PLAYER_UUID = "player_uuid";

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
        String sharePrefName = GAME_SETTINGS_FILE + ".xml";
        File f = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/" + sharePrefName);
        if (f.exists())
            return true;
        else
            return false;
    }

    /* Creates a username file */
    protected void createUsernameFile(String username2Save){
        SharedPreferences.Editor editor = getSharedPreferences(GAME_SETTINGS_FILE, MODE_PRIVATE).edit();
        editor.putString(PLAYER_NAME, username2Save);
        editor.putString(PLAYER_UUID, UUID.randomUUID().toString());
        editor.putInt(P2P_PORT, 9888);
        editor.putInt(MULTICAST_PORT, 9879);
        editor.putString(MULTICAST_IP_ADDRESS, "224.1.1.1");
        editor.commit();
    }

    /* Go to lobby activity */
    private void goToLobby() {
        /* Get stored username */
        SharedPreferences prefs = getSharedPreferences(GAME_SETTINGS_FILE, Context.MODE_PRIVATE);
        String deviceAddress = prefs.getString(PLAYER_UUID, null);
        String username = prefs.getString(PLAYER_NAME, null);
        PlayerInfo playerInfo = new PlayerInfo(username, deviceAddress);

        /* Start LobbyActivity*/
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(IntentType.MY_PLAYER_INFO, playerInfo);

        startActivity(intent);
        finish();
    }


}
