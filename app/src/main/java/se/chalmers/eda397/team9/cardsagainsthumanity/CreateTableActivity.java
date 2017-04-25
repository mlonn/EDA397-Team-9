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
import android.widget.EditText;

import android.widget.Toast;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ClientMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class CreateTableActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener{
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private List<WifiP2pDevice> peers;

    private Map<String, TableInfo> tables;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner tableList;
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
        setContentView(R.layout.activity_createtable);

        SharedPreferences prefs = this.getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        username = prefs.getString("name", null);

        tables = new HashMap<String, TableInfo>();

        tpresenter = new TablePresenter(this);

        final Button createTableButton = (Button) findViewById(R.id.createTable_button);
        final EditText tableName = (EditText)findViewById(R.id.tablename);
        final Button joinTableButton = (Button) findViewById(R.id.joinTable_button);
        final Button refreshButton = (Button) findViewById(R.id.refresh_button);
        tableList = (Spinner) findViewById(R.id.table_list);

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("multicastLock");
        tables = new HashMap<String, TableInfo>();

        //Broadcast
        initMulticast();

        //Initialzie p2p
        initP2p();
        System.out.println("Username: " + username);
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
                TableInfo table = tpresenter.createTable(tableName.getText().toString(), username);
                Intent intent = new Intent(v.getContext(), HostTableActivity.class);
                threadList.add(new TableMulticastSender().execute(s, group, table, port));

                try {
                    MulticastSocket s2;
                    s2 = new MulticastSocket(port);
                    s2.joinGroup(group);
                    threadList.add(new HostMulticastReceiver(multicastLock, s2, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                intent.putExtra("THIS.TABLE", table);

                startActivity(intent);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        greetAndReceive();
    }


    private void greetAndReceive(){
        threadList.add(new ClientMulticastReceiver(multicastLock, s, group, tpresenter).execute(tables, tableList, this));
    }

    private void joinGroup(MulticastSocket s, InetAddress group){
        try {
            if(!s.isBound())
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
        joinGroup(s, group);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
        try {
            s.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                //Example message (only for test)
                Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();

                try{
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                }
                catch(Exception e) {

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);

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
