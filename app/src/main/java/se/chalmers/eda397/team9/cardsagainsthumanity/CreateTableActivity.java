package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;

//Consider refactoring this class. I.e divide into several classes
public class CreateTableActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {


    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private Map<String, Table> tables;
    private Intent createTableIntent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();


    private WifiManager wifi;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s = null;
    InetAddress group = null;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tables = new HashMap<String, Table>();


        setContentView(R.layout.activity_createtable);

        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        final EditText tableName = (EditText)findViewById(R.id.tablename);
        final Button joinTableButton = (Button) findViewById(R.id.joinTable_button);
        final Button refreshButton = (Button) findViewById(R.id.refresh_button);
        final Intent intent = getIntent();
        final Spinner tableList = (Spinner) findViewById(R.id.table_list);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("multicastLock");
        tables = new HashMap<String, Table>();

        //Broadcast
        initMulticast();

        //Initialzie p2p
        initP2p();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discoverPeers();
                connect2Peers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Table table = new Table(tableName.getText().toString());
                tables.put(tableName.getText().toString(), table);
                Intent intent = new Intent(v.getContext(), CreateRuleActivity.class);
                new MulticastSender().execute(s, group, table, port);

                // startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MulticastReceiver().execute(s, tables, tableList, CreateTableActivity.this, multicastLock);;
            }
        });


        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreatePlayerListActivity.class);
                startActivity(intent);
            }
        });
    }


    private void discoverPeers() {
        wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(CreateTableActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(CreateTableActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connect2Peers() {
        for(final WifiP2pDevice device : peers) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            wifiManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                    String toastText = "Connected to " + device.deviceName;
                    System.out.println(toastText);
                }

                @Override
                public void onFailure(int reason) {
                    String toastText = "Failed to connect to  " + device.deviceName + " for reason " + reason;
                    System.out.println(toastText);
                }
            });
        }
    }



    /* register the broadcast receiver with the intent values to be matched */

    private void initMulticast(){
        try {
            group = InetAddress.getByName(ipAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            s = new MulticastSocket(port);
            s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
        try {
            if(!s.isBound())
                s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
        try {
            if(s.isBound())
                s.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initP2p() {
        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiBroadcastReceiver(wifiManager, channel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);




        registerReceiver(receiver, mIntentFilter);
        discoverPeers();
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Toast.makeText(CreateTableActivity.this, "No peers available",Toast.LENGTH_SHORT);
            return;
        }

    }
}
