package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

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
import se.chalmers.eda397.team9.cardsagainsthumanity.Serializer;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class GreetingMulticastSender extends AsyncTask<Object, Void, Void> {

    private MulticastSocket s = null;
    InetAddress group = null;

    @Override
    protected Void doInBackground(Object... objects) {
        for(Object current : objects) {
            if (current instanceof MulticastSocket)
                s = (MulticastSocket) current;
            if (current instanceof InetAddress)
                group = (InetAddress) current;
        }

        sendMulticast("CARDS_AGAINST_HUMANITY.GREETING");
        System.out.println("Sent a greeting!");
        return null;
    }

    private void sendMulticast(String greeting){
        byte[] msg = Serializer.serialize(greeting);
        DatagramPacket datagramMsg = new DatagramPacket(msg, msg.length, group, s.getLocalPort());
        try {
            s.send(datagramMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        cancel(true);
    }
}

