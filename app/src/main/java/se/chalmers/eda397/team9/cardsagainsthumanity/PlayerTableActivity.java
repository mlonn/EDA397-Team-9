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
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.PlayerMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.R;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerStatisticsFragment;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class PlayerTableActivity extends AppCompatActivity implements PropertyChangeListener{

    /* Multicast variables */
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private PlayerMulticastReceiver playerReceiver;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    /* Fragment variables */
    private FragmentManager fragmentManager;
    private PlayerStatisticsFragment psFragment;

    /* Class variables */
    private TableInfo tableInfo;
    private PlayerInfo myPlayerInfo;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_table);

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
        psFragment.addAllPlayers(tableInfo.getPlayerList());

        /* Multicast receiver */
        playerReceiver = (PlayerMulticastReceiver) getIntent().
                getSerializableExtra(IntentType.MULTICAST_RECEIVER);
        if(playerReceiver != null){
            playerReceiver.addPropertyChangeListener(this);
        }

        /* Initialize views */
        Button readyButton = (Button) findViewById(R.id.ready_button);
        Button leaveButton = (Button) findViewById(R.id.leave_button);

        /* View listeners */
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myPlayerInfo.isReady()) {
                    myPlayerInfo.setReady(false);
                    psFragment.setReady(myPlayerInfo, false);
                    //TODO: Consider interval sender
                    MulticastPackage ready = new MulticastPackage(tableInfo.getHost().getDeviceAddress(),
                            MulticastSender.Type.PLAYER_READY, myPlayerInfo);
                    new MulticastSender(ready, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    myPlayerInfo.setReady(true);
                    psFragment.setReady(myPlayerInfo, true);
                    MulticastPackage unReady = new MulticastPackage(tableInfo.getHost().getDeviceAddress(),
                            MulticastSender.Type.PLAYER_NOT_READY, myPlayerInfo);
                    new MulticastSender(unReady, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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


    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("TABLE_INTERVAL_UPDATE")){
            if(tableInfo.equals(propertyChangeEvent.getNewValue())){
                return;
            }
            psFragment.update((TableInfo) propertyChangeEvent.getNewValue());
        }

        if(propertyChangeEvent.getPropertyName().equals("SEND_PLAYER_UPDATE")){
            MulticastPackage playerUpdate = new MulticastPackage(tableInfo.getHost().getDeviceAddress(),
                    MulticastSender.Type.PLAYER_INTERVAL_UPDATE, myPlayerInfo);
            new MulticastSender(playerUpdate, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }

}
