package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
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
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ReliableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerStatisticsFragment;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;

public class HostTableActivity extends AppCompatActivity implements PropertyChangeListener{

    /* Multicast variables */
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;

    /* View variables*/
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
    private TableInfo myTableInfo;
    private List<PlayerInfo> playerList;
    private List<PlayerInfo> connectedPlayers;
    private Integer maxConnectionRetries = 0;

    /* Fragment variables */
    private FragmentManager fragmentManager;
    private PlayerStatisticsFragment psFragment;

    /* P2P Variables */
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private boolean receiverIsRegistered = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table_new);
        /* Remove ? */
        expansions = (ArrayList<CardExpansion>) getIntent().getExtras().get(IntentType.THIS_EXPANSIONS);
        players = new ArrayList<Player>();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        players.add(new Player(prefs.getString("name", null)));

        /* Initialize class variables */
        playerList = new ArrayList<>();
        colorList = new LinkedList<>(Arrays.asList(colorArray));
        p2pManager = new P2pManager(this);
        threadList = new ArrayList<>();
        connectedPlayers = new ArrayList<>();

        /* Initialize fragment variables */
        fragmentManager = getSupportFragmentManager();
        psFragment = (PlayerStatisticsFragment) fragmentManager.findFragmentById(R.id.playerFragment);

        /* Initialize views */
        startTableButton = (Button) findViewById(R.id.start_button);
        closeTableButton = (Button) findViewById(R.id.close_button);

        /* Initialize peer to peer and multicast socket */
        initP2p();
        initMulticastSocket();
        p2pManager.discoverPeers();

        /* Get table info */
        myTableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
        hostInfo = myTableInfo.getHost();
        addHost(hostInfo);

        /* Add dummy players */
    /*    for(int i = 0 ; i < 16 ; i++){
            PlayerInfo dummyPlayer = new PlayerInfo("Dummy "+ (i+1));
            addNewPlayer(dummyPlayer);
        }*/

        /* View Listeners */
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                Game game = new Game(players,expansions);
                intent.putExtra(IntentType.THIS_GAME, game);
                startActivity(intent);
                finish();
            }
        });
        closeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseTableDialog();
            }
        });
    }

    private void addHost(PlayerInfo hostInfo){
        assignRandomColor(hostInfo);
        playerList.add(hostInfo);
        psFragment.addHost(hostInfo);
    }

    /* Adds player and into the table */
    private void addNewPlayer(PlayerInfo newPlayer){
        assignRandomColor(newPlayer);
        myTableInfo.addPlayer(newPlayer);
        playerList.add(newPlayer);
        psFragment.addPlayer(newPlayer);
    }

    private void removePlayer(PlayerInfo player){
        colorList.add(player.getColor());
        myTableInfo.removePlayer(player);
        playerList.remove(player);
        psFragment.removePlayer(player);
    }

    /* Initialize peer to peer */
    private void initP2p() {
        p2pManager = new P2pManager(this);
        mIntentFilter = p2pManager.getIntentFilter();
        receiver = p2pManager.getReceiver();
        receiver.addPropertyChangeListener(this);
        if(!receiverIsRegistered) {
            registerReceiver(receiver, mIntentFilter);
        }
    }

    private void assignRandomColor(PlayerInfo playerInfo){
        int randomNumber = (int) (Math.random() * colorList.size());
        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);
        playerInfo.setColor(color);
    }

    /* Activiy overrides */

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        receiverIsRegistered = true;
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        receiverIsRegistered = false;
        super.unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiverIsRegistered)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMulticastSocket();
        if(!receiverIsRegistered) {
            registerReceiver(receiver, mIntentFilter);
        }
    }

    /* Method used for closing all async tasks and socket in this activity */
    //TODO: Concurrent modification exception
    private void closeConnection() {
        for (AsyncTask current : threadList) {
            if (!current.isCancelled())
                current.cancel(true);
        }

//        TODO: Find a way to close the socket safely
//        if(s != null || !s.isClosed())
//            s.close();
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

    private void openCloseTableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to close this table?").setTitle("Table: " + myTableInfo.getName());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        openCloseTableDialog();
    }

    /* Main menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        String username = prefs.getString("name", null);
        menu.findItem(R.id.profile).setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
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

    /* Stops p2p connection and receiver */
    private void stopP2p(){
        p2pManager.stopDiscoverPeers();
        p2pManager.disconnect();

    }

    /* Sends package using MulticastSender*/
    private void sendPackage(final String target, final String type, final Serializable object){
        Handler handler = new Handler(this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                MulticastPackage mPackage = new MulticastPackage(target,
                        type, object);
                threadList.add(new MulticastSender(mPackage, s, group).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            }
        });
    }

    private void sendReliablePackage(final MulticastPackage object, final MulticastPackage expectedResponse,
                                     final int maxRetries) {
        Handler handler = new Handler(this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                threadList.add(new ReliableMulticastSender(object, expectedResponse, maxRetries, s, group).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            }
        });
    }


    //TODO: Class starts to get big, might consider creating a handler class
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(Message.Type.MY_DEVICE_ADDRESS_FOUND)) {
            String deviceAddress = (String) propertyChangeEvent.getNewValue();
            stopP2p();

            TableInfo tableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
            tableInfo.getHost().setDeviceAddress(deviceAddress);

            HostMulticastReceiver hostMulticastReceiver = new HostMulticastReceiver(multicastLock, s, group, hostInfo);
            hostMulticastReceiver.addPropertyChangeListener(this);
            threadList.add(hostMulticastReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

            Toast.makeText(this, "Table opened", Toast.LENGTH_SHORT).show();
        }

        if(propertyChangeEvent.getPropertyName().equals(Message.Type.REQUEST_ALL_TABLES)){
            sendPackage(Message.Target.ALL_DEVICES,
                    Message.Response.HOST_TABLE, myTableInfo);
            Log.d("HostTableActivity", "Sent table!");
        }

        if(propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_JOIN_REQUEST)){
            final PlayerInfo newPlayer = (PlayerInfo) propertyChangeEvent.getNewValue();
            if(!myTableInfo.getPlayerList().contains(newPlayer)) {
                if(myTableInfo.getPlayerList().size() >= 19){
                    sendPackage(newPlayer.getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_DENIED, null);

                }else if(findPlayer(playerList, newPlayer) != null){
                    sendPackage(newPlayer.getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_DENIED, null);

                }else if(myTableInfo.getPlayerList().size() <= 19) {
                    //Updates view on the main UI thread
                    Handler mainHandler = new Handler(this.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                addNewPlayer(newPlayer);

                            }
                        });
                    connectedPlayers.add(newPlayer);
                    //TODO: Testing reliable multicast sender

                    myTableInfo.addPlayer(newPlayer);

                    MulticastPackage mPackage = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_ACCEPTED, myTableInfo);
                    MulticastPackage expectedResponse = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_CONFIRM, null);
                    sendReliablePackage(mPackage, expectedResponse, 7);
                }
            }
        }

        if(propertyChangeEvent.getPropertyName().equals(Message.Response.PLAYER_JOIN_CONFIRM)){
        }

        if(propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_TIMED_OUT)){
            removePlayer((PlayerInfo) propertyChangeEvent.getNewValue());
        }

        if(propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_INTERVAL_UPDATE)){
            PlayerInfo playerUpdate = (PlayerInfo) propertyChangeEvent.getNewValue();
            PlayerInfo player = findPlayer(playerList, playerUpdate);

            //Check if anything changed since last interval update of the player
            if(player == null){
                sendPackage(player.getDeviceAddress(),
                        Message.Response.PLAYER_DISCONNECTED, null);
                return;
            }

            if(findPlayer(connectedPlayers, player) == null){
                connectedPlayers.add(player);
            }
        }
    }

    /* Helper method to find player */
    private PlayerInfo findPlayer(List<PlayerInfo> list, PlayerInfo player){
        for(PlayerInfo current : list){
            if(current.getDeviceAddress() != null && current.getDeviceAddress().equals(player.getDeviceAddress()))
                return current;
        }
        return null;
    }
}
