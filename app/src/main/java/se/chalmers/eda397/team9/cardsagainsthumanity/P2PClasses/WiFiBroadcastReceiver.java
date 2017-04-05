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
    private WifiP2pManager.PeerListListener myPeerListListener;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    //Listener that currently prints the devices it can detect
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

            if(!refreshedPeers.equals(peers)){
                peers.clear();
                peers.addAll(refreshedPeers);
                //Trigger update
            }

            if (peers.size() == 0){
                System.out.println("No devices found");
                return;
            }

            for(final WifiP2pDevice device : peers){
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                wifiManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("connected to " + device.deviceName.toString());
                    }
                    @Override
                    public void onFailure(int reason) {
                        System.out.println("failed to connect");
                    }
                });
            }
        }
    };

    public WiFiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, CreateTableActivity activity) {
        this.wifiManager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    //Whenever a broadcast is received, do something depending on the broadcast type
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println(action);
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
                wifiManager.requestPeers(channel, peerListListener);
            }
            //Whenever peers changed we need to broadcast the table.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}

