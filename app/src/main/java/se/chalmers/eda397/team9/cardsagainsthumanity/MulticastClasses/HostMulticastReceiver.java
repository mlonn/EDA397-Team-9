package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.Serializer;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class HostMulticastReceiver extends MulticastReceiver<Object, Void, Void>{

    private MulticastSocket s;
    private Table table;
    private InetAddress group;
    private boolean keepGoing;

    public HostMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group) {
        super(mcLock);
        this.s = s;
        this.group = group;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        for(Object current : objects){
            if (current instanceof Table)
                table = (Table) current;
        }

        receiveAndSend();
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        System.out.println("Cancelled task!");
        keepGoing = false;

    }

    private void receiveAndSend() {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        keepGoing = true;

        try {
            s.setSoTimeout(5000);
        } catch (SocketException e) {
        }

        while (keepGoing && !isCancelled()) {
            Object inMsg = null;
            try {
                s.receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
                System.out.println("Message received: " + inMsg);
            } catch (IOException e) {
            }

            if(inMsg != null) {
                if (inMsg.toString().equals("CARDS_AGAINST_HUMANITY.GREETING")) {
                    new TableMulticastSender().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s, group, table);
                }
            }
        }
    }

}
