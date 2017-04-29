package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class MulticastSender extends AsyncTask {
    private MulticastSocket s;
    private InetAddress group;
    private MulticastPackage mPackage;

    public static class Type{
        public static final String GREETING = "greeting";
        public static final String HOST_TABLE = "host_table_info";
        public static final String PLAYER_JOIN_ACCEPTED = "player_join_accepted";
        public static final String PLAYER_JOIN_DENIED = "player_join_denied";
        public static final String PLAYER_JOIN_REQUEST = "player_join_request";
        public static final String TABLE_INTERVAL_UPDATE = "table_interval_update";
        public static final String PLAYER_JOIN_SUCCESS = "table_join_success";
    }

    public static class Target{
        public static final String ALL_DEVICES = "all_devices";
    }

    public MulticastSender(MulticastPackage mPackage, MulticastSocket s, InetAddress group){
        this.s = s;
        this.group = group;
        this.mPackage = mPackage;
    }

    protected InetAddress getGroup(){
        return group;
    }

    protected MulticastSocket getSocket(){
        return s;
    }

    protected MulticastPackage getMPackage(){
        return mPackage;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        byte[] msg = Serializer.serialize(mPackage);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, s.getLocalPort());
        try {
            if(s != null || !s.isClosed())
                s.send(datagramMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("MultiSender", "Sent a " + mPackage.getPackageType() + " to " + mPackage.getTarget());
        return null;
    }


}
