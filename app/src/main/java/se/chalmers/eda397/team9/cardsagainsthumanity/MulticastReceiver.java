package se.chalmers.eda397.team9.cardsagainsthumanity;

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
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;

public class MulticastReceiver extends AsyncTask<Object, Void, Map<String, Table>> {

    private MulticastSocket s = null;
    private Map<String, Table> tables;
    private Spinner tableList;
    private AppCompatActivity activity;
    private WifiManager.MulticastLock mcLock;

    @Override
    protected Map<String, Table> doInBackground(Object... objects) {

        for(Object current : objects){
            if(current instanceof MulticastSocket)
                s = (MulticastSocket) current;
            if(current instanceof Map)
                tables = (Map<String, Table>) current;
            if(current instanceof Spinner)
                tableList = (Spinner) current;
            if(current instanceof AppCompatActivity)
                activity = (AppCompatActivity) current;
            if(current instanceof WifiManager.MulticastLock)
                mcLock = (WifiManager.MulticastLock) current;
        }
        receiveAndRegisterTable();
        return tables;
    }


    private void receiveAndRegisterTable(){
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        boolean keepGoing = true;
        int counter = 0;
        int marginOfError = 2;
        startMulticastLock();

        try {
            s.setSoTimeout(1000);
        } catch (SocketException e) {
            counter++;
        }

        while(keepGoing) {
            Table msg;
            try {
                s.receive(recv);
                msg = (Table)deserialize(recv.getData());
            } catch (IOException e) {
                msg = null;
                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }


            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg != null) {
                String hostName = msg.getHost();
                String tableName = msg.getName();
                String tableSize = "" + msg.getSize();

                System.out.println("Host: " + hostName +
                        "\nTable: " + tableName +
                        "\nSize: " + tableSize);

                tables.put(tableName, msg);
                System.out.println(tables.size());
            }
        }
    }

    private Object deserialize(byte[] serializedObject) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObject);
        ObjectInput in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return o;
    }

    private void startMulticastLock(){
        if(!mcLock.isHeld()) {
            mcLock.setReferenceCounted(true);
            mcLock.acquire();
        }
    }

    private void endMulticastLock(){
        if(mcLock != null && mcLock.isHeld()){
            mcLock.release();
        }
    }

    @Override
    protected void onPostExecute(Map<String, Table> tables) {
        endMulticastLock();

        List<Table> list = new ArrayList<>();
        for(Map.Entry<String,Table> current : tables.entrySet()){
            list.add(current.getValue());
        }

        ArrayAdapter<Table> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableList.setAdapter(spinnerAdapter);
    }

}
