package se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;

public class WiFiBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;
    private String deviceAddress;
    private PropertyChangeSupport pcs;


    public WiFiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener) {
        this.wifiManager = manager;
        this.channel = channel;
        this.peerListListener = peerListListener;
        pcs = new PropertyChangeSupport(this);
    }

    //Whenever a broadcast is received, do something depending on the broadcast type
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("WifiBRec", "Wifi P2P is enabled");
            } else {
                Log.d("WifiBRec", "Wifi P2P is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wifiManager != null) {
                    wifiManager.requestPeers(channel, peerListListener);
            }
            //Whenever peers changed we need to broadcast the table.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            //Retreive the P2p MAC-address of this device
            if(deviceAddress == null) {
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                deviceAddress = device.deviceAddress;
                pcs.firePropertyChange(Message.Type.MY_DEVICE_ADDRESS_FOUND, null, deviceAddress);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        pcs.addPropertyChangeListener(pcl);
    }

    protected String getMyDeviceAddress(){
        return deviceAddress;
    }
}

