package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Submission;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class GameMulticastReciever extends MulticastReceiver {

    private PlayerInfo myPlayerInfo;
    private boolean isJoined;
    private TableInfo table;

    public GameMulticastReciever(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                   InetAddress group, PlayerInfo myPlayerInfo, boolean isJoined, TableInfo table) {
        super(mcLock, s, group);
        this.myPlayerInfo = myPlayerInfo;
        this.table = table;
        this.isJoined = isJoined;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getPropertyChangeSupport().firePropertyChange(Message.Type.START_REFRESHING, 0, 1);
        getPropertyChangeSupport().firePropertyChange(Message.Type.REQUEST_TABLE, 0, 1);
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        int counter = 0;
        int maxCount = 3;

        while (!isCancelled() && counter < maxCount) {
            byte[] buf = new byte[100000];
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
                if (!isJoined) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.REQUEST_TABLE, 0, 1);
                    counter++;
                }
            }

            if (msg instanceof MulticastPackage) {
                String target = ((MulticastPackage) msg).getTarget();
                String type = ((MulticastPackage) msg).getPackageType();
                Object packageObject = ((MulticastPackage) msg).getObject();

                Log.d("GameMultiRec", "Received a " + type + " with destination " + target);
                if (packageObject instanceof Submission) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.SUBMISSION,0,packageObject);
                }
                if (type.equals(Message.Type.SELECTED_WINNER)) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.SELECTED_WINNER, 0 , packageObject);
                }
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        Log.d("GameMultiRec", "Receiver cancelled");
        super.onCancelled();
    }
}
