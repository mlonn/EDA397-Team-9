package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public class HostMulticastReceiver extends MulticastReceiver<Object, Void, Void>{

    private TableInfo table;
    private boolean keepGoing;

    public HostMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group) {
        super(mcLock, s, group);
    }

    @Override
    protected Void doInBackground(Object... objects) {
        for(Object current : objects){
            if (current instanceof TableInfo)
                table = (TableInfo) current;
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
            getSocket().setSoTimeout(5000);
        } catch (SocketException e) {
        }

        while (keepGoing && !isCancelled()) {
            Object inMsg = null;
            try {
                getSocket().receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
                System.out.println("Message received: " + inMsg);
            } catch (IOException e) {
            }

            if(inMsg != null) {
                if (inMsg.toString().equals("CARDS_AGAINST_HUMANITY.GREETING")) {
                    new TableMulticastSender().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, super.getSocket(), super.getGroup(), table);
                }
            }
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        System.out.println("Cancelled HostMutlicastReceiver!");
    }
}
