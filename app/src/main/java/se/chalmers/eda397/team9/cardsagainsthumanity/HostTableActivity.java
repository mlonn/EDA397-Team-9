package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerRowLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class HostTableActivity extends AppCompatActivity implements PropertyChangeListener{

    /* Multicast variables */
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;

    /* View variables*/
    private GridLayout playerGridLayout;
    private Button startTableButton;
    private Button closeTableButton;

    /* Color variables */
    String[] colorArray = {
            "#f8c82d", "#fbcf61", "#ff6f6f",
            "#e3a712", "#e5ba5a", "#d1404a",
            "#0dccc0", "#a8d164", "#3498db",
            "#0ead9a", "#27ae60", "#2980b9",
            "#d49e99", "#b23f73", "#48647c",
            "#74525f", "#832d51", "#2c3e50",
            "#e84b3a", "#fe7c60", "#ecf0f1",
            "#c0392b", "#404148", "#bdc3c7"};

    LinkedList<String> colorList;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    /* Class variables */
    private ArrayList<Player> players;
    private ArrayList<CardExpansion> expansions;
    private PlayerInfo hostInfo;

    /* P2P Variables */
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table);

        expansions = (ArrayList<CardExpansion>) getIntent().getExtras().get("THIS.EXPANSIONS");
        players = new ArrayList<Player>();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        players.add(new Player(prefs.getString("name", null)));

        colorList = new LinkedList<>(Arrays.asList(colorArray));


        /* Initialize class variables */
        p2pManager = new P2pManager(this);
        colorList = new LinkedList<>(Arrays.asList(colorArray));
        threadList = new ArrayList<>();

        /* Initialize views */
        startTableButton = (Button) findViewById(R.id.start_button);
        closeTableButton = (Button) findViewById(R.id.close_button);
        playerGridLayout = (GridLayout) findViewById(R.id.playerlist_grid);

        /* Initialize peer to peer and multicast socket */
        initP2p();
        initMulticastSocket();

        p2pManager.discoverPeers();

        /* Get table info */
        TableInfo tableInfo = (TableInfo) getIntent().getSerializableExtra("THIS.TABLE");
        hostInfo = tableInfo.getHost();

        addHostRow(hostInfo.getName());

        /* Add dummy players */
        for(int i = 0 ; i < 18 ; i++){
            addPlayerRow("DummyPlayer");
        }

        /* View Listeners */
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                Game game = new Game(players,expansions);
                intent.putExtra("THIS.GAME", game);
                startActivity(intent);
                finish();
            }
        });
        closeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //closeConnection();
                finish();

            }
        });
    }

    /* Initialize peer to peer */
    private void initP2p() {
        p2pManager = new P2pManager(this);
        mIntentFilter = p2pManager.getIntentFilter();
        receiver = p2pManager.getReceiver();
        receiver.addPropertyChangeListener(this);
        registerReceiver(receiver, mIntentFilter);

        p2pManager.discoverPeers();
    }

    /* Adds a host row */
    private void addHostRow(String name){
        PlayerRowLayout hostRow = new PlayerRowLayout(this);
        hostRow.setName(name);
        hostRow.setAsHost();

        int randomNumber = (int) (Math.random() * colorList.size());
        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);

        hostRow.setImageColor(color);
        playerGridLayout.addView(hostRow);
    }

    /* Adds a player row */
    private void addPlayerRow(String name){
        int randomNumber = (int) (Math.random() * colorList.size());
        PlayerRowLayout playerRow = new PlayerRowLayout(this);
        playerRow.setName(name);

        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);

        playerRow.setImageColor(color);
        playerGridLayout.addView(playerRow);
    }

    /* Activiy overrides */
    @Override
    protected void onPause() {
        super.onPause();
        //closeConnection();
        unregisterReceiver(receiver);
        Log.d("HostTableActivity", "Connection closed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMulticastSocket();
        Log.d("HostTableActivity", "Connection opened");
    }

    /* Method used for closing all async tasks and socket in this activity*/
    private void closeConnection() {
        for (AsyncTask current : threadList) {
            if (!current.isCancelled())
                current.cancel(true);
        }

        s.close();
    }

    /* Method for initializing the multicast socket*/
    private void initMulticastSocket() {

        if(multicastLock == null || !multicastLock.isHeld()) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("multicastLock");
        }

        if(s == null || s.isClosed()) {
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

    /*
    * Listens to WiFiBroadcastReceiver, whenever it receives the MAC address (of this device),
    * it will put the address into the host PlayerInfo class tableInfo. Then it will
    * start hosting the table.
    * */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("DEVICE_ADDRESS_FOUND")){
            String deviceAddress = (String) propertyChangeEvent.getNewValue();

            TableInfo tableInfo = (TableInfo) getIntent().getSerializableExtra("THIS.TABLE");
            tableInfo.getHost().setDeviceAddress(deviceAddress);

            threadList.add(new HostMulticastReceiver(multicastLock, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tableInfo));
            Toast.makeText(this, "Table opened", Toast.LENGTH_SHORT).show();
        }
    }
}
