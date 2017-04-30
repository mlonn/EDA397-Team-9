package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerStatisticsFragment;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class PlayerTableActivity extends AppCompatActivity {

    /* Fragment variables */
    private FragmentManager fragmentManager;
    private PlayerStatisticsFragment psFragment;

    /* Class variables */
    private TableInfo tableInfo;
    private boolean ready = false;
    private PlayerInfo myPlayerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_table);

        /* Initialize fragment */
        fragmentManager = getSupportFragmentManager();
        psFragment = (PlayerStatisticsFragment) fragmentManager.findFragmentById(R.id.playerFragment);

        /* Retrieve intent data */
        tableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
        myPlayerInfo = (PlayerInfo) getIntent().getSerializableExtra(IntentType.MY_PLAYER_INFO);

        /* Initialize the playerlist */
        psFragment.addHost(tableInfo.getHost());
        psFragment.addAllPlayers(tableInfo.getPlayerList());

        /* Initialize views */
        Button readyButton = (Button) findViewById(R.id.ready_button);
        Button leaveButton = (Button) findViewById(R.id.leave_button);

        /* View listeners */
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ready)
                    psFragment.setReady(myPlayerInfo, false);
                else
                    psFragment.setReady(myPlayerInfo, true);

                ready = !ready;
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /* Activity overrides */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /* Main menu */
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
