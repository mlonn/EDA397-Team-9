package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ClientMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSpinner;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSwipeRefreshLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class LobbyActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener{

    /* Class variables */
    private Map<String, TableInfo> tables;
    private FindTableSwipeRefreshLayout swipeRefreshLayout;
    private FindTableSpinner tableSpinner;
    private String username;
    private List<AsyncTask> threadList = new ArrayList<>();
    private TablePresenter tpresenter;
    private PlayerInfo myPlayerInfo;

    /* Multicast variables */
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private InetAddress group;

    /* WifiP2P variables */
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private List<WifiP2pDevice> peers;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        /* Initialize P2p */
        initP2p();

        /* Get my player information from intent */
        myPlayerInfo = (PlayerInfo) getIntent().getSerializableExtra("PLAYER_INFO");

        /* Initialize presenter (Used to separate view from model) */
        tpresenter = new TablePresenter(this);

        /* Initialize variables, views and layouts */
        tables = new HashMap<String, TableInfo>();
        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        final Button joinTableButton = (Button) findViewById(R.id.joinTable_button);
        tableSpinner = (FindTableSpinner) findViewById(R.id.table_list);
        swipeRefreshLayout = (FindTableSwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setProgressViewOffset(true, 500, 700);

        /* Initialize multicast socket */
        initMulticastSocket();

        /* Set listeners to views and layouts*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(LobbyActivity.this, "Searching for tables...", Toast.LENGTH_SHORT).show();

                ClientMulticastReceiver greeting = greetAndReceive();
                greeting.addPropertyChangeListener(swipeRefreshLayout);

            }
        });

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateTableActivity.class);
                intent.putExtra("PLAYER_INFO", myPlayerInfo);
                startActivity(intent);
            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO needs to be correctly implemented. Not tested yet.
                //When peers are found, p2pmanager
                //connects to the host of the selected table as well
                p2pManager.discoverPeers();

            }
        });
    }

    /* PeerListListener overrides */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        System.out.println("Found peer!");

        connectToSelectedTableHost();

        if (peers.size() == 0) {
            Toast.makeText(LobbyActivity.this, "No peers available", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /* WifiP2p functions */
    private void initP2p() {
        p2pManager = new P2pManager(this);
        mIntentFilter = p2pManager.getIntentFilter();
        receiver = p2pManager.getReceiver();
        peers = new ArrayList<WifiP2pDevice>();

        registerReceiver(receiver, mIntentFilter);
        p2pManager.discoverPeers();
    }

    private void connectToSelectedTableHost(){
        final TableInfo selectedTable = (TableInfo) tableSpinner.getSelectedItem();

        if(selectedTable == null){
            return;
        }

        /* Set up config for connection */
        WifiP2pConfig config = new WifiP2pConfig();

        //Used to check whether WifiP2p can find the host as well before connecting
        WifiP2pDevice hostDevice = getTableHostDevice(peers, selectedTable.getHost().getDeviceAddress());


        System.out.println("Host device address from selected table: " + selectedTable.getHost().getDeviceAddress());
        System.out.println("Host device address from method: " + hostDevice.deviceName);
        for(WifiP2pDevice current: peers){
            System.out.println("Peers: " + current.deviceName);
        }

        if(hostDevice == null){
            return;
        }

        config.deviceAddress = hostDevice.deviceAddress;

        /* Connect to host of selected table*/
        p2pManager.connect(config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                String toastText = "Connected to " + selectedTable.getHost().getName();
                System.out.println(toastText);

                Intent intent = new Intent(LobbyActivity.this, PlayerTableActivity.class);
                //startActivity(intent);
            }

            @Override
            public void onFailure(int reason) {
                String toastText = "Failed to connect to  " + selectedTable.getName() + " for reason " + reason;
                System.out.println(toastText);
            }
        });
    }

    private WifiP2pDevice getTableHostDevice(List<WifiP2pDevice> allPeers, String hostAdress) {
        TableInfo selectedTable = (TableInfo) tableSpinner.getSelectedItem();
        if(selectedTable == null){
            return null;
        }

        String hostAddress = selectedTable.getHost().getDeviceAddress();

        for(WifiP2pDevice current : allPeers){
            if(current.deviceAddress.equals(hostAddress)){
                return current;
            }
        }

        System.out.println("The host with device address " + selectedTable.getHost().getDeviceAddress() +
                ", and game name " + selectedTable.getHost().getName() + " could not be found.");
        return null;
    }



    /* Initialize the multicast */

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
        initMulticastSocket();
    }

    @Override
    public void onPause(){
        super.onPause();
        s.close();
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
