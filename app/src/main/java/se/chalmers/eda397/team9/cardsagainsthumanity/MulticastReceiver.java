package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
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
    private Context applicationContext;
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
            if(current instanceof Context)
                applicationContext = (Context) current;
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
            String[] msg;
            try {
                s.receive(recv);
                msg = new String(recv.getData(), recv.getOffset(), recv.getLength()).split("_");
            } catch (IOException e) {
                msg = new String[]{};
                System.out.println("Trying to receive datagram again (try " + counter + ")");
                counter++;
            }


            if(counter > marginOfError) {
                keepGoing = false;
                System.out.println("Done");
            }

            if (msg.length == 3) {
                String hostName = msg[0];
                String tableName = msg[1];
                String tableSize = msg[2];

                System.out.println("Host: " + hostName +
                        "\nTable: " + tableName +
                        "\nSize: " + tableSize);

                Table newTable = new Table(tableName);
                tables.put(tableName, newTable);
                System.out.println(tables.size());
            }
        }
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

        ArrayAdapter<Table> spinnerAdapter = new ArrayAdapter<>(applicationContext, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableList.setAdapter(spinnerAdapter);
    }

}
