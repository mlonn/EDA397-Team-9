package se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.CreateTableActivity;

/**
 * Created by Alex on 2017-03-31.
 */

public class WiFiBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private CreateTableActivity activity;


    public WiFiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, CreateTableActivity activity) {
        this.wifiManager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    //Whenever a broadcast is received, do something depending on the broadcast type
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(intent.hasCategory("CARDS_AGAINST_HUMANITY")){
            if(intent.getAction() == "WIFI_NEW_TABLE_INFO"){
                //Currently only prints out the table information
                System.out.println("Table: " + intent.getStringExtra("TABLE_NAME")
                );
            }
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("Wifi P2P is enabled");
            } else {
                System.out.println("Wifi P2P is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wifiManager != null) {
                wifiManager.requestPeers(channel, activity);
            }
            //Whenever peers changed we need to broadcast the table.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}

