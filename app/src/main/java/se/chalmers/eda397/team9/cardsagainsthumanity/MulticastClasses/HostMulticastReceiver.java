package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class HostMulticastReceiver extends MulticastReceiver<Object, Void, Void>{

    private PlayerInfo hostInfo;
    private Map<PlayerInfo, Integer> connectingPlayers;
    private final int maxRetries = 5;

    public HostMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                 InetAddress group, PlayerInfo hostInfo) {
        super(mcLock, s, group);
        this.hostInfo = hostInfo;
        connectingPlayers = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Object... objects) {

        /* Handles receive message and send message */
        byte[] buf = new byte[10000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        try {
            getSocket().setSoTimeout(700);
        } catch (SocketException e) {
        }

        /* Keep trying to retrieve messages until cancelled */
        while (!isCancelled()) {
            Object inMsg = null;
            try {
                getSocket().receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                 /* If join acceptance from player doesn't arrive, try sending out the table again.
                    After maxRetries, stop sending and inform listeners that the player timed out */
                if(!connectingPlayers.isEmpty()) {
                    for (Map.Entry<PlayerInfo, Integer> current : connectingPlayers.entrySet()) {
                        if (current.getValue() < maxRetries) {
                            current.setValue(current.getValue() + 1);
                            getPropertyChangeSupport().firePropertyChange("PLAYER_JOIN_REQUESTED",
                                    null, current.getKey());
                        } else {
                            connectingPlayers.remove(current.getKey());
                            getPropertyChangeSupport().firePropertyChange("PLAYER_TIMED_OUT",
                                    null, current.getKey());
                        }
                    }
                }
            }

            handleMessage(inMsg);
        }
        return null;
    }

    private void handleMessage(Object inMsg){
        if (inMsg instanceof MulticastPackage) {
            String targetAddress = (String) ((MulticastPackage) inMsg).getTarget();
            String packageType = (String) ((MulticastPackage) inMsg).getPackageType();
            Object packageObject = ((MulticastPackage) inMsg).getObject();

            if (targetAddress.equals(MulticastSender.Target.ALL_DEVICES))
                if(packageType.equals(MulticastSender.Type.GREETING))
                    getPropertyChangeSupport().firePropertyChange("TABLE_REQUESTED", 0, 1);

            if (targetAddress.equals(hostInfo.getDeviceAddress())) {
                if (packageType.equals(MulticastSender.Type.PLAYER_JOIN_REQUEST)) {
                    getPropertyChangeSupport().firePropertyChange("PLAYER_JOIN_REQUESTED",
                            null, packageObject);
                    connectingPlayers.put((PlayerInfo) packageObject, 0);
                    Log.d("HMR", "Player " + ((PlayerInfo) packageObject).getName() + " sent join request");
                }
                if (packageType.equals(MulticastSender.Type.PLAYER_JOIN_SUCCESS))
                    getPropertyChangeSupport().firePropertyChange("PLAYER_JOIN_SUCCESSFUL",
                            null, packageObject);
                    connectingPlayers.remove(packageObject);
            }
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Log.d("HostMultRec", "Receiver cancelled");
    }
}
