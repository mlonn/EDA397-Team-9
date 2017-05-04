package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.os.AsyncTask;
import android.util.Log;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;

public class ReliableMulticastSender extends AsyncTask{

    private MulticastSocket s;
    private InetAddress group;
    private MulticastPackage mPackage;
    private MulticastPackage expectedResponse;
    private AsyncTask receiver;
    private PropertyChangeSupport pcs;
    private int maxCount;

    public ReliableMulticastSender(MulticastPackage mPackage, MulticastPackage expectedResponse,
                                        MulticastSocket s, InetAddress group){
        this(mPackage, expectedResponse, 5, s, group);
    }

    public ReliableMulticastSender(MulticastPackage mPackage, MulticastPackage expectedResponse,
                                   Integer maxRetries, MulticastSocket s, InetAddress group){
        this.s = s;
        this.group = group;
        this.mPackage = mPackage;
        this.expectedResponse = expectedResponse;
        pcs = new PropertyChangeSupport(this);
        maxCount = maxRetries;
    }

    private void send(){
        byte[] msg = Serializer.serialize(mPackage);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, s.getLocalPort());
        try {
            if(s != null || !s.isClosed())
                s.send(datagramMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
            Log.d("MultiSender", "Sent a " + mPackage.getPackageType() + " to " + mPackage.getTarget());
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        int counter = 0;
        send();
        try {
            s.setSoTimeout(500);
        } catch (SocketException e) {
        }

        while(!isCancelled() || counter < maxCount){
            byte[] buf = new byte[10000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            Object inMsg = null;

            try {
                s.receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if(counter < maxCount) {
                    counter++;
                    send();
                }else{
                    return null;
                }
            }

            if(inMsg instanceof MulticastPackage){
                String target = ((MulticastPackage) inMsg).getTarget();
                String type = ((MulticastPackage) inMsg).getPackageType();
                Object object = ((MulticastPackage) inMsg).getObject();

                if(target.equals(expectedResponse.getTarget())){
                    if(type.equals(expectedResponse.getPackageType())){
                        pcs.firePropertyChange(type, 0, object);
                        return null;
                    }
                }
            }

        }
        return null;
    }
}
