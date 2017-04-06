package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class MulticastSender extends AsyncTask<Object, Void, Void> {

    private MulticastSocket s = null;
    InetAddress group = null;
    int port;
    Table table;

    @Override
    protected Void doInBackground(Object... objects) {

        for(Object current : objects) {
            if (current instanceof MulticastSocket)
                s = (MulticastSocket) current;
            if (current instanceof InetAddress)
                group = (InetAddress) current;
            if (current instanceof Integer)
                port = (Integer) current;
            if(current instanceof Table){
                table = (Table) current;
            }
        }
        sendMulticast(table);
        System.out.println("Sent!");
        cancel(true);
        return null;
    }

    private void sendMulticast(Table table){
        byte[] msg = serialize(table);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, port);
        try {
            s.send(datagramMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Object object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] array = {};
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            array = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return array;
    }

}

