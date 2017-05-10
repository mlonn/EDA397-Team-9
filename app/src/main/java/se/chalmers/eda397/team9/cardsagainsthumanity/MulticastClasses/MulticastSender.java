package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class MulticastSender extends AsyncTask {
    private int packetSize;
    private MulticastSocket s;
    private InetAddress group;
    private MulticastPackage mPackage;
    public MulticastSender(MulticastPackage mPackage, MulticastSocket s, InetAddress group, int packetSize){
        this(mPackage,s,group);
        this.packetSize = packetSize;

    }
    public MulticastSender(MulticastPackage mPackage, MulticastSocket s, InetAddress group){
        this.s = s;
        this.group = group;
        this.mPackage = mPackage;
        this.packetSize = 10000;
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
