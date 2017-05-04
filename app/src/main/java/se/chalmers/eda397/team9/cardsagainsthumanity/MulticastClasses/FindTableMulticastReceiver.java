package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class FindTableMulticastReceiver extends MulticastReceiver<Object, Void, Map<String, TableInfo>>{

    private Map<String, TableInfo> tables;
    private Map<String, TableInfo> newTables;
    private TablePresenter tablePresenter;

    public FindTableMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s,
                                      InetAddress group, TablePresenter tablePresenter,
                                      Map<String, TableInfo> tables) {
        super(mcLock, s, group);
        this.tablePresenter = tablePresenter;
        this.tables = tables;
        newTables = new HashMap<>();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getPropertyChangeSupport().firePropertyChange(Message.Type.REQUEST_ALL_TABLES, 0, 1);
        getPropertyChangeSupport().firePropertyChange(Message.Type.START_REFRESHING, 0, 1);
    }

    @Override
    protected Map<String, TableInfo> doInBackground(Object... objects) {
        receiveAndRegisterTable();
        return tables;
    }

    /* Receives messages from tables and registers them */
    private void receiveAndRegisterTable(){
        byte[] buf = new byte[10000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        boolean keepGoing = true;
        int counter = 1;
        int marginOfError = 3;

        try {
            getSocket().setSoTimeout(1000);
        } catch (SocketException e) {
            counter++;
        }

        /* Keeps receiving messages until the thread is cancelled */
        while(keepGoing && !isCancelled()) {
            Object msg = null;
            try {
                getSocket().receive(recv);
                msg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if(/* !newTables.equals(tables) && */ counter < marginOfError) {
                    getPropertyChangeSupport().firePropertyChange(Message.Type.REQUEST_ALL_TABLES, 0, 1);
                }
                Log.d("CMReceiver", "Trying to receive datagram again (try " + counter + ")");
                counter++;
            }

            if(counter > marginOfError) {
                keepGoing = false;
                Log.d("CMReceiver", "Done");
            }

            if (msg instanceof MulticastPackage) {
                String targetAddress = ((MulticastPackage) msg).getTarget();
                String packageName = ((MulticastPackage) msg).getPackageType();
                Object packageObject = ((MulticastPackage) msg).getObject();

                Log.d("FindTableReceiver", packageName);

                if(targetAddress.equals(Message.Target.ALL_DEVICES)){
                    if(packageName.equals(Message.Response.HOST_TABLE)){
                        Log.d("CMReceiver", "Message received: " + msg);
                        newTables.put(((TableInfo) packageObject).getName(), (TableInfo) packageObject);
                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Map<String, TableInfo> tables) {
        super.onPostExecute(tables);
        getPropertyChangeSupport().firePropertyChange(Message.Type.STOP_REFRESHING, 0, 1);
        updateTable();
    }

    private void updateTable(){
        tablePresenter.clearTables();
        tablePresenter.insertAll(newTables);

        tables.clear();
        tables.putAll(newTables);

        List<TableInfo> list = new ArrayList<>();
        for(Map.Entry<String,TableInfo> current : tables.entrySet()){
            list.add(current.getValue());
        }

        getPropertyChangeSupport().firePropertyChange(Message.Type.SPINNER_UPDATE_TABLE, null, list);
    }

    @Override
    protected void onCancelled() {
        Log.d("FTMultReceiver", "Receiver cancelled");
        super.onCancelled();
    }
}
