package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;

/**
 * Created by Alex on 2017-03-31.
 * A placeholder activity for testing P2P discover and find devices
 */

public class PeerToPeerActivity extends AppCompatActivity {


    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiBroadcastReceiver(wifiManager, channel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discoverPeers();

        setContentView(R.layout.activity_main);

    }

    /*register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void discoverPeers(){
        wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
                System.out.println("Discovery successful");
            }
            @Override
            public void onFailure(int reason) {
                System.out.println("Discovery failed, " + reason );
            }
        });
    }
}
