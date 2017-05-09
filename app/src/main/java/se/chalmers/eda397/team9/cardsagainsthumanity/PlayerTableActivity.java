package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.BlackCard;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.PlayerMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerStatisticsFragment;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;

public class PlayerTableActivity extends AppCompatActivity implements PropertyChangeListener {

    /* P2p variables */
    private P2pManager p2pManager;
    private int p2pPort;

    /* Multicast variables */
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private PlayerMulticastReceiver playerReceiver;
    private String ipAdress;
    private int port;

    /* Fragment variables */
    private FragmentManager fragmentManager;
    private PlayerStatisticsFragment psFragment;

    /* Class variables */
    private boolean gameStarted;
    private TableInfo tableInfo;
    private PlayerInfo myPlayerInfo;
    private ArrayList<CardExpansion> expansions;

    private ArrayList<PlayerInfo> playerList;
    private IntentFilter mIntentFilter;
    private WiFiBroadcastReceiver receiver;
    private ArrayList<WifiP2pDevice> peers;
    private BlackCard blackCard;
    private PlayerInfo king;


    /* Method for initializing the multicast socket*/
    private void initMulticastSocket() {
        if (multicastLock == null || !multicastLock.isHeld()) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("multicastLock");
        }

        if (s == null || s.isClosed()) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_table);

        /* Initialize multicast and p2p port/ip */
        SharedPreferences preferences = getSharedPreferences(IndexActivity.GAME_SETTINGS_FILE, Context.MODE_PRIVATE);
        ipAdress = preferences.getString(IndexActivity.MULTICAST_IP_ADDRESS, null);
        port = preferences.getInt(IndexActivity.MULTICAST_PORT, 0);
        p2pPort = preferences.getInt(IndexActivity.P2P_PORT, 0);

        /* Initialize multicast */
        initMulticastSocket();

        /* Initialize fragment */
        fragmentManager = getSupportFragmentManager();
        psFragment = (PlayerStatisticsFragment) fragmentManager.findFragmentById(R.id.playerFragment);

        /* Retrieve intent data */
        tableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
        myPlayerInfo = (PlayerInfo) getIntent().getSerializableExtra(IntentType.MY_PLAYER_INFO);

        /* Initialize the playerlist */
        psFragment.addHost(tableInfo.getHost());
        psFragment.initializePlayers(tableInfo);

        /* Multicast receiver */
        playerReceiver = new PlayerMulticastReceiver(multicastLock, s, group, myPlayerInfo, true,tableInfo);
        playerReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (playerReceiver != null) {
            playerReceiver.addPropertyChangeListener(this);
        }

        /* Initialize views */
        Button leaveButton = (Button) findViewById(R.id.leave_button);

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        expansions = new ArrayList<CardExpansion>();

        gameStarted = false;
    }

    /* Activity overrides */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void sendPackage(final MulticastPackage mPackage){
        android.os.Handler handler = new android.os.Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new MulticastSender(mPackage, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        });
    }

    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        //If other player joins successfully, update my table
        switch (propertyChangeEvent.getPropertyName()) {
            case Message.Type.GAME_STARTED:
                MulticastPackage mPackage = new MulticastPackage(tableInfo.getHost().getDeviceAddress(),
                        Message.Response.GAME_START_CONFIRMED, myPlayerInfo);
                sendPackage(mPackage);
                break;
            case Message.Type.PLAYER_LIST:
                playerList = (ArrayList<PlayerInfo>) propertyChangeEvent.getNewValue();
                break;
            case Message.Type.EXPANSION_LIST:
                expansions = CardHandler.getExpansions(this);
                ArrayList<CardExpansion> exp = new ArrayList<CardExpansion>();
                ArrayList<String> expansionNames  = (ArrayList<String>) propertyChangeEvent.getNewValue();
                for (CardExpansion e : expansions){
                    for(String s : expansionNames){
                        if(e.getName().equals(s)){
                            exp.add(e);
                        }
                    }
                }
                expansions = exp;
                break;
            case Message.Type.KING:
                king = (PlayerInfo) propertyChangeEvent.getNewValue();
                break;
            case Message.Type.BLACK_CARD:
                blackCard = (BlackCard) propertyChangeEvent.getNewValue();
                break;
            case Message.Response.OTHER_PLAYER_JOIN_ACCEPTED:
                tableInfo = (TableInfo) propertyChangeEvent.getNewValue();

                android.os.Handler handler = new android.os.Handler(getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        psFragment.update(tableInfo);
                    }
                });
                break;
            default: break;
        }
        if (playerList != null && expansions != null && blackCard != null && king != null && !gameStarted) {
            Intent intent = new Intent(PlayerTableActivity.this.getApplicationContext(), GameActivity.class);
            intent.putExtra(IntentType.THIS_GAME, new Game(playerList,expansions,king,blackCard));
            intent.putExtra(IntentType.TABLE_ADDRESS, tableInfo.getHost().getDeviceAddress());
            intent.putExtra(IntentType.THIS_TABLE, tableInfo);
            startActivity(intent);
            gameStarted = true;
            finish();
        }
    }
}
