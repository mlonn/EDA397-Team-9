package se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class P2pManager {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter mIntentFilter;
    private AppCompatActivity activity;
    WiFiBroadcastReceiver receiver;

    public P2pManager(AppCompatActivity activity){
        this.activity = activity;
        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);

        if(activity instanceof WifiP2pManager.PeerListListener) {
            receiver = new WiFiBroadcastReceiver(manager, channel, (WifiP2pManager.PeerListListener) activity);
        }else{
            throw new IllegalArgumentException("The input activity does not implement WifiP2pManager.PeerListListener");
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

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

    public void connect2Peers(List<WifiP2pDevice> peers ) {
        for(final WifiP2pDevice device : peers) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    String toastText = "Connected to " + device.deviceName;
                    System.out.println(toastText);
                }

                @Override
                public void onFailure(int reason) {
                    String toastText = "Failed to connect to  " + device.deviceName + " for reason " + reason;
                    System.out.println(toastText);
                }
            });
        }
    }
}
