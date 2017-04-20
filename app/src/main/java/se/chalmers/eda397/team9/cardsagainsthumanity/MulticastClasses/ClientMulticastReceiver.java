package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.Serializer;

public class ClientMulticastReceiver extends MulticastReceiver<Object, Void, Map<String, Table>>{

    private MulticastSocket s;
    private Map<String, Table> tables;
    private Spinner tableList;
    private AppCompatActivity activity;
    private InetAddress group;
    private Map<String, Table> newTables;

    public ClientMulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group) {
        super(mcLock);
        this.s = s;
        this.group = group;
    }

    @Override
    protected Map<String, Table> doInBackground(Object... objects) {
        for(Object current : objects){
            if(current instanceof Map)
                tables = (Map<String, Table>) current;
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

        boolean keepGoing = true;
        int counter = 1;
        int marginOfError = 3;
        startMulticastLock();

        try {
            s.setSoTimeout(2000);
        } catch (SocketException e) {
            counter++;
        }

        while(keepGoing && !isCancelled()) {
            Object msg = null;
            try {
                s.receive(recv);
                msg = Serializer.deserialize(recv.getData());
            } catch (IOException e) {
                if(newTables.equals(tables) && counter < marginOfError){
                    new GreetingMulticastSender().execute(s, group);
                }
                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }

            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg instanceof Table) {
                System.out.println(msg);
                newTables.put(((Table) msg).getName(), (Table)msg);
            }
        }
    }

    @Override
    protected void onPostExecute(Map<String, Table> tables) {
        super.onPostExecute(tables);
        updateTable();
    }


    private void updateTable(){
        tables.clear();
        tables.putAll(newTables);

        List<Table> list = new ArrayList<>();
        for(Map.Entry<String,Table> current : tables.entrySet()){
            list.add(current.getValue());
        }

        ArrayAdapter<Table> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableList.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onPreExecute() {
        new GreetingMulticastSender().execute(s, group);
        super.onPreExecute();
    }
}
