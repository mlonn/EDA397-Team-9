package se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class P2pManager {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter mIntentFilter;
    private AppCompatActivity activity;
    WiFiBroadcastReceiver receiver;

    public P2pManager(AppCompatActivity activity){



        /* Initialize variables */
        this.activity = activity;
        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);

        /* Initialize receiver */
        if(activity instanceof WifiP2pManager.PeerListListener) {
            receiver = new WiFiBroadcastReceiver(manager, channel, (WifiP2pManager.PeerListListener) activity);
        }else{
            receiver = new WiFiBroadcastReceiver(manager, channel, null);
        }

        /* Add intent filters */
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    public void connect(WifiP2pConfig config, WifiP2pManager.ActionListener actionListener){
        manager.connect(channel, config, actionListener);
    }

    public void disconnect(){
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
                Log.d("P2pManager", "P2P disconnected");
            }

            @Override
            public void onFailure(int i) {
                Log.d("P2pManager", "P2P failed to disconnect");
            }
        });
    }


    public IntentFilter getIntentFilter(){
        return mIntentFilter;
    }

    public WiFiBroadcastReceiver getReceiver(){
        return receiver;
    }

    public void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(activity, "Discovery successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(activity, "Discovery failed: " + reasonCode, Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void stopDiscoverPeers(){
        manager.stopPeerDiscovery(channel, null);
    }

    public void connect2Peers(List<WifiP2pDevice> peers ) {
        for(final WifiP2pDevice device : peers) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    String toastText = "Connected to " + device.deviceName;
                    Log.d("P2pManager", toastText);
                }

                @Override
                public void onFailure(int reason) {
                    String toastText = "Failed to connect to  " + device.deviceName + " for reason " + reason;
                    Log.d("P2pManager", toastText);
                }
            });
        }
    }
}
