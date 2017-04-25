package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;

public class PlayerTableActivity extends AppCompatActivity {

    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private List<WifiP2pDevice> peers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_table);

        ListView listview1 = (ListView) findViewById(R.id.player_list_view1);
        ListView listview2 = (ListView) findViewById(R.id.player_list_view2);

        ArrayList<String> listItems = new ArrayList<String>();

        /*Set up p2p*/
        initP2p();
        p2pManager.discoverPeers();

        //TODO: Switch 'peers' from all possible peers to the peers in the same table
        p2pManager.connect2Peers(peers);

        listItems.add("Alex");
        listItems.add("Mister Yi");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listItems.add("Muhaka");
        listview1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
    }

    /* Activity overrides */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /* Peer to peer functions*/
    private void initP2p() {
        p2pManager = new P2pManager(this);
        mIntentFilter = p2pManager.getIntentFilter();
        receiver = p2pManager.getReceiver();
        peers = new ArrayList<WifiP2pDevice>();

        registerReceiver(receiver, mIntentFilter);
        p2pManager.discoverPeers();
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
                //Do something

                //Example message (only for test)
                Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
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
