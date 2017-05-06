package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.os.AsyncTask;
import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class HostMulticastSender extends AsyncTask {

    private MulticastSocket s;
    private InetAddress group;
    private MulticastPackage mPackage;
    private MulticastPackage expectedResponse;
    private PropertyChangeSupport pcs;
    private int maxCount;
    private Map<PlayerInfo, Boolean> playerMap;

    public HostMulticastSender(MulticastPackage mPackage, MulticastPackage expectedResponse,
                               MulticastSocket s, InetAddress group, List<PlayerInfo> playerList) {
        this(mPackage, expectedResponse, 10, s, group);
        this.playerMap = new HashMap<PlayerInfo, Boolean>();
        for (PlayerInfo p : playerList) {
            this.playerMap.put(p, false);
        }

        System.out.println(playerList.size());
        System.out.println(playerMap.size());
    }

    public HostMulticastSender(MulticastPackage mPackage, MulticastPackage expectedResponse,
                               Integer maxRetries, MulticastSocket s, InetAddress group) {
        this.s = s;
        this.group = group;
        this.mPackage = mPackage;
        this.expectedResponse = expectedResponse;
        pcs = new PropertyChangeSupport(this);
        maxCount = maxRetries;

    }

    private void send() {

        byte[] msg = Serializer.serialize(mPackage);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, s.getLocalPort());
        try {
            if (s != null || !s.isClosed())
                s.send(datagramMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("ReliableMultiSender", "Sent a " + mPackage.getPackageType() + " to " + mPackage.getTarget());
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        /* For testing purposes */
        if(playerMap.size() == 0) {
            pcs.firePropertyChange(Message.Response.ALL_CONFIRMED, 0, 1);
            //TODO: change to pcs.firePropertyChange(Message.Response.GAME_START_DENIED, 0, 1);
            return null;
        }

        int counter = 0;
        send();
        try {
            s.setSoTimeout(500);
        } catch (SocketException e) {
        }

        while (!isCancelled() || counter < maxCount) {
            byte[] buf = new byte[10000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            Object inMsg = null;

            try {
                s.receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if (counter < maxCount) {
                    counter++;
                    send();
                } else {
                    return null;
                }
            }

            if (inMsg instanceof MulticastPackage) {

                String target = ((MulticastPackage) inMsg).getTarget();
                String type = ((MulticastPackage) inMsg).getPackageType();
                Object object = ((MulticastPackage) inMsg).getObject();

                if (target.equals(expectedResponse.getTarget())) {
                    System.out.println("TARGET");
                    if (type.equals(expectedResponse.getPackageType())) {
                        System.out.println("TYPE");
                        for (Map.Entry<PlayerInfo, Boolean> current : playerMap.entrySet()) {
                            if (current.getKey().getDeviceAddress().equals(((PlayerInfo) object).getDeviceAddress())) {
                                System.out.println("EQUALS");
                                current.setValue(true);
                            }
                        }
                        boolean allConnected = true;
                        System.out.println(playerMap.entrySet().size());
                        for (Map.Entry<PlayerInfo, Boolean> current : playerMap.entrySet()) {
                            if (!current.getValue()) {
                                allConnected = false;
                            }
                        }
                        if (allConnected) {
                            pcs.firePropertyChange(Message.Response.ALL_CONFIRMED,0,1);
                        }
                        return null;
                    }
                }
            }

        }
        return null;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
}
