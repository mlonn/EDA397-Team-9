package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class TableMulticastSender extends AsyncTask<Object, Void, Void> {
    private MulticastSocket s;
    InetAddress group;
    TableInfo table;


    @Override
    protected Void doInBackground(Object... objects) {
        for(Object current : objects) {
            if (current instanceof MulticastSocket) {
                s = (MulticastSocket) current;
            }
            if (current instanceof InetAddress) {
                group = (InetAddress) current;
            }
            if(current instanceof TableInfo){
                table = (TableInfo) current;
            }
        }

        sendMulticast(table);
        cancel(true);
        return null;
    }

    private void sendMulticast(TableInfo table){
        byte[] msg = Serializer.serialize(table);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, s.getLocalPort());
        try {
            s.send(datagramMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void v){
        cancel(true);
    }
}

