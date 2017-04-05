package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;

//Consider refactoring this class. I.e divide into several classes
public class CreateTableActivity extends AppCompatActivity {


    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private Map<String, Table> tables;
    private Intent createTableIntent;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tables = new HashMap<String, Table>();
        setContentView(R.layout.activity_createtable);
        initP2p();
        Intent intent = getIntent();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initP2p();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        Button joinTableButton = (Button) findViewById(R.id.joinTable_button);

        final EditText tableName = (EditText) findViewById(R.id.tablename);

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Table table = new Table(tableName.getText().toString());
                tables.put(tableName.getText().toString(), table);
                Intent intent = new Intent(v.getContext(), CreateRuleActivity.class);


                //Creates an intent with table information
                createTableIntent = new Intent("WIFI_NEW_TABLE_INFO");
                createTableIntent.addCategory("CARDS_AGAINST_HUMANITY");
                createTableIntent.putExtra("TABLE_NAME", table.getName());
                // createTableIntent.putExtra("HOST_NAME", table.getHost());
                // createTableIntent.putExtra("TABLE_SIZE", table.getSize());
                //Broadcasts this intent
                sendBroadcast(createTableIntent);

            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreatePlayerListActivity.class);
                startActivity(intent);
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void initP2p() {

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiBroadcastReceiver(wifiManager, channel, this);

        mIntentFilter = new IntentFilter();

        mIntentFilter.addCategory("CARDS_AGAINST_HUMANITY");
        mIntentFilter.addAction("WIFI_NEW_TABLE_INFO");

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                System.out.println("Discovery successful");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Discovery failed, " + reason);
            }
        });

    }

    

}
