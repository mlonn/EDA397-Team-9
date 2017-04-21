package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;
import android.widget.Spinner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ClientMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSpinner;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSwipeRefreshLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class LobbyActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener{
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private List<WifiP2pDevice> peers;

    private Map<String, TableInfo> tables;
    private FindTableSwipeRefreshLayout swipeRefreshLayout;
    private FindTableSpinner tableSpinner;
    private String username;

    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private InetAddress group;
    private List<AsyncTask> threadList = new ArrayList<>();
    private TablePresenter tpresenter;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        SharedPreferences prefs = this.getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        username = prefs.getString("name", null);

        tables = new HashMap<String, TableInfo>();

        tpresenter = new TablePresenter(this);

        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        final Button joinTableButton = (Button) findViewById(R.id.joinTable_button);
        tableSpinner = (FindTableSpinner) findViewById(R.id.table_list);

        tables = new HashMap<String, TableInfo>();

        /* Initialize multicast */
        initMulticastSocket();
        greetAndReceive();

        /* Initialzie p2p */
        initP2p();
        System.out.println("Username: " + username);
        swipeRefreshLayout = (FindTableSwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ClientMulticastReceiver greeting = greetAndReceive();
                greeting.addPropertyChangeListener(swipeRefreshLayout);
                p2pManager.discoverPeers();
                p2pManager.connect2Peers(peers);
                swipeRefreshLayout.setRefreshing(true);

                Toast.makeText(LobbyActivity.this, "Searching for tables...", Toast.LENGTH_SHORT).show();

            }
        });

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateTableActivity.class);
                startActivity(intent);
            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerTableActivity.class);
                startActivity(intent);
            }
        });
    }

    /* initialize the multicast */

    private void initMulticastSocket(){

        multicastLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).
                createMulticastLock("multicastLock");

        if(s == null || s.isClosed()) {
            try {
                group = InetAddress.getByName(ipAdress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                s = new MulticastSocket(port);
                joinGroup(s, group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ClientMulticastReceiver greetAndReceive(){
        ClientMulticastReceiver greeting = (ClientMulticastReceiver)
                new ClientMulticastReceiver(multicastLock, s, group, tpresenter)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,tables, tableSpinner, this);

        greeting.addPropertyChangeListener(tableSpinner);

        threadList.add(greeting);
        return greeting;
    }


    private void joinGroup(MulticastSocket s, InetAddress group){
        try {
            s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Activity overrides*/

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
        initMulticastSocket();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
        s.close();
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

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Toast.makeText(LobbyActivity.this, "No peers available",Toast.LENGTH_SHORT).show();
            return;
        }
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
