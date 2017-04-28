package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class HostMulticastReceiver extends MulticastReceiver<Object, Void, Void>{

    private TableInfo tableInfo;

    public HostMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                 InetAddress group, TableInfo tableInfo) {
        super(mcLock, s, group);
        this.tableInfo = tableInfo;
    }

    @Override
    protected Void doInBackground(Object... objects) {

        /* Handles receive message and send message */
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        try {
            getSocket().setSoTimeout(5000);
        } catch (SocketException e) {
        }

        /* Keep trying to retrieve messages until cancelled */
        while (!isCancelled()) {
            Object inMsg = null;
            try {
                getSocket().receive(recv);
                inMsg = Serializer.deserialize(recv.getData());
                Log.d("HostMultRec", "Message received: " + inMsg);
            } catch (IOException e) {
                //Handle exception?
            }
            //Handle the message
            handleMessage(inMsg);
        }
        return null;
    }

    private void sendTable(){
        MulticastPackage mPackage = new MulticastPackage(MulticastSender.Target.ALL_DEVICES,
                MulticastSender.Type.HOST_TABLE, tableInfo);
        new MulticastSender(mPackage, getSocket(), getGroup()).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleMessage(Object inMsg){
        if (inMsg instanceof MulticastPackage){
            String targetAddress = (String) ((MulticastPackage) inMsg).getTarget();
            String packageName = (String) ((MulticastPackage) inMsg).getPackageType();
            Object packageObject = ((MulticastPackage) inMsg).getObject();

            if(targetAddress.equals(MulticastSender.Target.ALL_DEVICES))
                if(packageName.equals(MulticastSender.Type.GREETING))
                    sendTable();

            if(targetAddress.equals(tableInfo.getHost().getDeviceAddress()))
                if(packageName.equals(MulticastSender.Type.PLAYER_JOIN_REQUEST))
                    addPlayerToTable((PlayerInfo) packageObject);
        }
    }

    private void addPlayerToTable(PlayerInfo playerInfo){
        if(!tableInfo.getPlayerList().contains(playerInfo)) {
            //TODO: Max size?
            tableInfo.addPlayer(playerInfo);
            //TODO: Accept player by multicasting
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Log.d("HostMultRec", "Cancelled HostMutlicastReceiver!");
    }
}
