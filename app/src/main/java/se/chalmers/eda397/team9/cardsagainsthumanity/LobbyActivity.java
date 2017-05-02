package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.FindTableMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.PlayerMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSpinner;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.FindTableSwipeRefreshLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class LobbyActivity extends AppCompatActivity implements PropertyChangeListener{

    /* Class variables */
    private Map<String, TableInfo> tables;
    private FindTableSwipeRefreshLayout swipeRefreshLayout;
    private FindTableSpinner tableSpinner;
    private Map<Integer, AsyncTask> threadMap = new HashMap<Integer, AsyncTask>();
    private TablePresenter tpresenter;
    private PlayerInfo myPlayerInfo;
    private TableInfo selectedTable;

    /* MulticastSenders and MulticastReceivers */
    private final Integer FIND_TABLE_RECEIVER = 0;
    private final Integer PLAYER_RECEIVER = 1;
    private final Integer TABLE_REQUEST_SENDER = 2;
    private final Integer TABLE_REQUEST_RETRY = 3;
    private final Integer PLAYER_ACCEPTED = 4;

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
                Toast.makeText(LobbyActivity.this, "Searching for tables...", Toast.LENGTH_SHORT).show();
                FindTableMulticastReceiver greeting = greetAndReceive();

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
                if(!(selectedTable instanceof TableInfo)){
//                    Toast.makeText(LobbyActivity.this, "No table is selected", Toast.LENGTH_SHORT).show();
//                    return;
                    /* For testing purposes */
                    TableInfo fakeTable = new TableInfo("Cool", new PlayerInfo("Cool Host", "test_address"));
                    fakeTable.addPlayer(new PlayerInfo("Dummy1", "test_address", "#abcdef"));
                    fakeTable.addPlayer(new PlayerInfo("Dummy2", "test_address", "#1aaeed"));
                    fakeTable.addPlayer(new PlayerInfo("Dummy3", "test_address", "#1ea03e"));
                    fakeTable.addPlayer(myPlayerInfo);

                    Intent intent = new Intent(LobbyActivity.this, PlayerTableActivity.class);
                    intent.putExtra(IntentType.THIS_TABLE, fakeTable);
                    intent.putExtra(IntentType.MY_PLAYER_INFO, myPlayerInfo);
                    startActivity(intent);
                    return;
                }

                Toast.makeText(LobbyActivity.this, "Attempting to join selected table", Toast.LENGTH_SHORT).show();
                MulticastReceiver playerMulticastReceiver = new PlayerMulticastReceiver(multicastLock,
                        s, group, myPlayerInfo);
                playerMulticastReceiver.addPropertyChangeListener(tableSpinner);
                playerMulticastReceiver.addPropertyChangeListener(LobbyActivity.this);
                threadMap.put(PLAYER_RECEIVER, playerMulticastReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
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

    /* Starts the FindTableMulticastReceiver */
    private FindTableMulticastReceiver greetAndReceive(){
        FindTableMulticastReceiver greeting = (FindTableMulticastReceiver)
                new FindTableMulticastReceiver(multicastLock, s, group, tpresenter, tables)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        greeting.addPropertyChangeListener(tableSpinner);
        greeting.addPropertyChangeListener(this);

        threadMap.put(FIND_TABLE_RECEIVER, greeting);
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
        closeConnection();
    }

    public void closeConnection(){
        for(Map.Entry<Integer, AsyncTask> current : threadMap.entrySet()) {
            if(!current.getValue().getStatus().equals(AsyncTask.Status.FINISHED))
                if(!current.getValue().isCancelled())
                    if(current.getKey() != PLAYER_RECEIVER)
                        current.getValue().cancel(true);
        }
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
                try{
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                } catch (Exception e){

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);
                return true;
            case R.id.changeTable:
                //Do something
                return true;
            case R.id.settings:
                //Do something
                return true;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! I'm playing this wonderful game called King of Cards. Please download it you too from Play store so we can play together!";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "King of Cards");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("REQUEST_TABLES")){
            MulticastPackage mPackage = new MulticastPackage(MulticastSender.Target.ALL_DEVICES,
                    MulticastSender.Type.GREETING);
            threadMap.put(TABLE_REQUEST_SENDER, new MulticastSender(mPackage, s, group).execute());
            Log.d("LobbyActivity", "Requesting tables...");
        }

        if(propertyChangeEvent.getPropertyName().equals("NO_RESPONSE")){
            Toast.makeText(this, "No response from host", Toast.LENGTH_SHORT).show();
        }

        if(propertyChangeEvent.getPropertyName().equals("REQUEST_TABLE")){
            if(selectedTable == null){
                throw new NullPointerException("Cannot request table information from a null object");
            }
            MulticastPackage mPackage = new MulticastPackage(selectedTable.getHost().getDeviceAddress(),
                    MulticastSender.Type.PLAYER_JOIN_REQUEST, myPlayerInfo);
            threadMap.put(TABLE_REQUEST_RETRY, new MulticastSender(mPackage, s, group).execute());
        }

        if(propertyChangeEvent.getPropertyName().equals("PLAYER_ACCEPTED")){
            TableInfo hostTable = ((TableInfo) propertyChangeEvent.getNewValue());
            PlayerInfo newPlayerInfo = findPlayer(hostTable.getPlayerList(), myPlayerInfo);

            if(newPlayerInfo != null)
                myPlayerInfo = newPlayerInfo;

            MulticastPackage mPackage = new MulticastPackage(selectedTable.getHost().getDeviceAddress(),
                    MulticastSender.Type.PLAYER_JOIN_SUCCESS, myPlayerInfo);
            threadMap.put(PLAYER_ACCEPTED, new MulticastSender(mPackage, s, group).execute());

            Intent intent = new Intent(this, PlayerTableActivity.class);
            intent.putExtra(IntentType.THIS_TABLE, hostTable);
            intent.putExtra(IntentType.MY_PLAYER_INFO, myPlayerInfo);
//            intent.putExtra(IntentType.MULTICAST_RECEIVER, (MulticastReceiver) threadMap.get(PLAYER_RECEIVER));
//            startActivity(intent);
        }

        if(propertyChangeEvent.getPropertyName().equals("PLAYER_DENIED")){
            Toast.makeText(this, "Table is full", Toast.LENGTH_SHORT).show();
        }
    }

    private PlayerInfo findPlayer(List<PlayerInfo> list, PlayerInfo player){
        for(PlayerInfo current : list){
            if(player.getDeviceAddress().equals(current.getDeviceAddress()))
                return current;
        }
        return null;
    }
}
