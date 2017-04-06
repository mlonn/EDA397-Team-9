package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class MulticastSender extends AsyncTask<Object, Void, Void> {

    private MulticastSocket s = null;
    InetAddress group = null;
    int port;
    String tableInfo;

    @Override
    protected Void doInBackground(Object... objects) {

        for(Object current : objects) {
            if (current instanceof MulticastSocket)
                s = (MulticastSocket) current;
            if (current instanceof InetAddress)
                group = (InetAddress) current;
            if (current instanceof Integer)
                port = (Integer) current;
            if(current instanceof String){
                tableInfo = (String) current;
            }
        }
        sendMulticast(tableInfo);
        System.out.println("Sent!");
        cancel(true);
        return null;
    }

    private void sendMulticast(String msg){
        DatagramPacket datagramMsg = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
        try {
            s.send(datagramMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
