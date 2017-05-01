package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class PlayerMulticastReceiver extends MulticastReceiver{

    private PlayerInfo myPlayerInfo;
    private boolean isJoined = false;

    public PlayerMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                   InetAddress group, PlayerInfo myPlayerInfo) {
        super(mcLock, s, group);
        this.myPlayerInfo = myPlayerInfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getPropertyChangeSupport().firePropertyChange("START_REFRESHING", 0, 1);
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        int counter = 0;
        int maxCount = 3;

        while(!isCancelled() && counter < maxCount) {
            byte[] buf = new byte[10000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            Object msg = null;

            try {
                getSocket().setSoTimeout(500);
            } catch (SocketException e) {
            }

            try {
                getSocket().receive(recv);
                msg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if(!isJoined){
                    getPropertyChangeSupport().firePropertyChange("REQUEST_TABLE_RETRY", 0 ,1);
                    counter++;
                }else{
                    getPropertyChangeSupport().firePropertyChange("SEND_PLAYER_UPDATE", 0, 1);
                }
            }

            if(msg instanceof MulticastPackage) {
                String target = ((MulticastPackage) msg).getTarget();
                String type = ((MulticastPackage) msg).getPackageType();
                Object packageObject = ((MulticastPackage) msg).getObject();

                if(target.equals(myPlayerInfo.getDeviceAddress())) {
                    if (type.equals(MulticastSender.Type.PLAYER_JOIN_ACCEPTED)) {
                        getPropertyChangeSupport().firePropertyChange("PLAYER_ACCEPTED",
                                null, packageObject);
                        isJoined = true;
                        getPropertyChangeSupport().firePropertyChange("STOP_REFRESHING", 0, 1);
                    }
                    if (type.equals(MulticastSender.Type.PLAYER_JOIN_DENIED)) {
                        getPropertyChangeSupport().firePropertyChange("TABLE_FULL", 0, 1);
                        getPropertyChangeSupport().firePropertyChange("STOP_REFRESHING", 0, 1);
                        cancel(true);
                    }
                    if (type.equals(MulticastSender.Type.TABLE_INTERVAL_UPDATE))
                        getPropertyChangeSupport().firePropertyChange("TABLE_INTERVAL_UPDATE",
                                null, packageObject);
                    if (type.equals(MulticastSender.Type.PLAYER_INTERVAL_UPDATE))
                        getPropertyChangeSupport().firePropertyChange("PLAYER_INTERVAL_UPDATE", null, packageObject);
                }
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        Log.d("PlayerMultReceiver", "Receiver cancelled");
        getPropertyChangeSupport().firePropertyChange("STOP_REFRESHING", 0, 1);
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Object result) {
        getPropertyChangeSupport().firePropertyChange("STOP_REFRESHING", 0, 1);
        getPropertyChangeSupport().firePropertyChange("NO_RESPONSE", 0, 1);
    }
}
