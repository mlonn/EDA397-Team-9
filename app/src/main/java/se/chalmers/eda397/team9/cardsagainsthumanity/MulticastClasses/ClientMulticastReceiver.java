package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class ClientMulticastReceiver extends MulticastReceiver<Object, Void, Map<String, TableInfo>>{

    private Map<String, TableInfo> tables;
    private Spinner tableList;
    private AppCompatActivity activity;
    private Map<String, TableInfo> newTables;
    private TablePresenter tablePresenter;

    public ClientMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group, TablePresenter tablePresenter) {
        super(mcLock, s, group);
        this.tablePresenter = tablePresenter;
    }

    @Override
    protected Map<String, TableInfo> doInBackground(Object... objects) {
        for(Object current : objects){
            if(current instanceof Map)
                tables = (Map<String, TableInfo>) current;
            if(current instanceof Spinner)
                tableList = (Spinner) current;
            if(current instanceof AppCompatActivity)
                activity = (AppCompatActivity) current;
            }

        newTables = new HashMap<>();
        receiveAndRegisterTable();
        return tables;
    }


    private void receiveAndRegisterTable(){
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
//        Toast.makeText(activity, "Searching for tables...", Toast.LENGTH_SHORT).show();

        boolean keepGoing = true;
        int counter = 1;
        int marginOfError = 3;
        startMulticastLock();

        try {
            getSocket().setSoTimeout(2000);
        } catch (SocketException e) {
            counter++;
        }

        while(keepGoing && !isCancelled()) {
            Object msg = null;
            try {
                getSocket().receive(recv);
                msg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if(newTables.equals(tables) && counter < marginOfError){
                    new GreetingMulticastSender().execute(getSocket(), getGroup());
                }
                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }

            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg instanceof TableInfo) {
                System.out.println(msg);
                newTables.put(((TableInfo) msg).getName(), (TableInfo)msg);
            }
        }
    }

    @Override
    protected void onPostExecute(Map<String, TableInfo> tables) {
        super.onPostExecute(tables);
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

        ArrayAdapter<TableInfo> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableList.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onPreExecute() {
        new GreetingMulticastSender().execute(getSocket(), getGroup());
        super.onPreExecute();
    }
}
