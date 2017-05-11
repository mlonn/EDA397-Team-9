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

    private String from;
    private PlayerInfo myPlayerInfo;
    private TableInfo table;

    public GameMulticastReciever(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                   InetAddress group, PlayerInfo myPlayerInfo, TableInfo table, String from) {
        super(mcLock, s, group);
        this.myPlayerInfo = myPlayerInfo;
        this.table = table;
        this.from = from;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getPropertyChangeSupport().firePropertyChange(Message.Type.START_REFRESHING, 0, 1);
        getPropertyChangeSupport().firePropertyChange(Message.Type.REQUEST_TABLE, 0, 1);
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        while (!isCancelled()) {
            System.out.println(from);
            byte[] buf = new byte[100000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            Object msg = null;

            try {
                getSocket().setSoTimeout(1500);
            } catch (SocketException e) {
            }

            try {
                getSocket().receive(recv);
                msg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
            }
            if (msg instanceof MulticastPackage) {
                String target = ((MulticastPackage) msg).getTarget();
                String type = ((MulticastPackage) msg).getPackageType();
                Object packageObject = ((MulticastPackage) msg).getObject();

                Log.d("GameMultiRec", "Received a " + type + " with destination " + target + " from " + from);
                if (packageObject instanceof Submission) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.SUBMISSION,0,packageObject);
                }
                if (type.equals(Message.Type.SELECTED_WINNER)) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.SELECTED_WINNER, 0 , packageObject);
                }
                if (type.equals(Message.Response.RECEIVED_WINNER)) {
                    getPropertyChangeSupport().firePropertyChange(Message.Response.RECEIVED_WINNER,0,1);
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
