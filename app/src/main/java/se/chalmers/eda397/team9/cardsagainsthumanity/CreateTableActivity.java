package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ClientMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.GreetingMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;

//Consider refactoring this class. I.e divide into several classes
public class CreateTableActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener{
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private List<WifiP2pDevice> peers;

    private Map<String, Table> tables;
    private Intent createTableIntent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner tableList;

    private WifiManager wifi;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s = null;
    private InetAddress group = null;
    private List<AsyncTask> threadList = new ArrayList<>();

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
        tableList = (Spinner) findViewById(R.id.table_list);

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
                p2pManager.discoverPeers();
                p2pManager.connect2Peers(peers);
                swipeRefreshLayout.setRefreshing(false);
                for (AsyncTask current : threadList) {
                    System.out.println(current.getClass().getSimpleName() + ": isCancelled - " + current.isCancelled() );
                }
            }
        });

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Table table = new Table(tableName.getText().toString());
                tables.put(tableName.getText().toString(), table);
                Intent intent = new Intent(v.getContext(), CreateRuleActivity.class);
                threadList.add(new TableMulticastSender().execute(s, group, table, port));

                try {
                    MulticastSocket s2;
                    s2 = new MulticastSocket(port);
                    s2.joinGroup(group);
                    threadList.add(new HostMulticastReceiver(multicastLock, s2, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                greetAndReceive();
            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreatePlayerListActivity.class);
                startActivity(intent);
            }
        });
    }

    /* initialize the multicast */

    private void initMulticast(){
        try {
            group = InetAddress.getByName(ipAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            s = new MulticastSocket(port);
            s.joinGroup(group);
            System.out.println("LASDASD: " + s.getLocalSocketAddress());
            greetAndReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greetAndReceive(){
        threadList.add(new ClientMulticastReceiver(multicastLock, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tables, tableList, this));
    }

    private void joinGroup(MulticastSocket s, InetAddress group){
        try {
            if(!s.isBound())
                s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leaveGroup(MulticastSocket s, InetAddress group){
        try {
            if(s.isBound())
                s.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(AsyncTask current : threadList){
            current.cancel(true);
        }

    }

    /* Activity overrides*/
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
        joinGroup(s, group);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
        leaveGroup(s, group);
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
            Toast.makeText(CreateTableActivity.this, "No peers available",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
