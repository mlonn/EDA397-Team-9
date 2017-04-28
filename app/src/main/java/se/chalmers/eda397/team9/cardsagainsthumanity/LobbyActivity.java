package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSpinner;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSwipeRefreshLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class LobbyActivity extends AppCompatActivity{

    /* Class variables */
    private Map<String, TableInfo> tables;
    private FindTableSwipeRefreshLayout swipeRefreshLayout;
    private FindTableSpinner tableSpinner;
    private List<AsyncTask> threadList = new ArrayList<>();
    private TablePresenter tpresenter;
    private PlayerInfo myPlayerInfo;
    private TableInfo selectedTable;

    /* Multicast variables */
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private InetAddress group;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

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

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTable = (TableInfo) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTable = null;
            }
        });

        createTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateTableActivity.class);
                intent.putExtra(IntentType.MY_PLAYER_INFO, myPlayerInfo);
                startActivity(intent);
            }
        });

        joinTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO needs to be correctly implemented. Not tested yet.

                Log.d("LobbyActivity", ((TableInfo) tableSpinner.getSelectedItem()).getHost().getDeviceAddress());
                sendJoinRequest(selectedTable);
            }
        });
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

    /* Sends join request to given table */
    private void sendJoinRequest(TableInfo targetTable){
        if(selectedTable == null){
            throw new NullPointerException("The given TableInfo cannot be null");
        }
        MulticastPackage mPackage = new MulticastPackage(targetTable.getHost().getDeviceAddress(),
                MulticastSender.Type.PLAYER_JOIN_REQUEST, myPlayerInfo);
        threadList.add(new MulticastSender(mPackage, s, group).execute());
    }

    /* Starts the ClientMulticastReceiver */
    private ClientMulticastReceiver greetAndReceive(){
        ClientMulticastReceiver greeting = (ClientMulticastReceiver)
                new ClientMulticastReceiver(multicastLock, s, group, tpresenter, tables)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
