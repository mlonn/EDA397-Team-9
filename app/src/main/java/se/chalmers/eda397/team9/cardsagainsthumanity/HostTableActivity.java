package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class HostTableActivity extends Activity {


    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;

    String ipAdress = "224.1.1.1";
    int port = 9879;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table);
        threadList = new ArrayList<>();
        initMulticastSocket();
        openConnection();

    }

    @Override
    protected void onPause() {
        super.onPause();
        closeConnection();
        System.out.println("Connection closed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMulticastSocket();
        System.out.println("Connection opened");
    }

    private void openConnection(){
        TableInfo table = (TableInfo) getIntent().getExtras().get("THIS.TABLE");
        threadList.add(new TableMulticastSender().execute(s, group, table, port));
        threadList.add(new HostMulticastReceiver(multicastLock, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table));
        Toast.makeText(this, "Table opened", Toast.LENGTH_SHORT).show();

        initMulticastSocket();

    }

    private void closeConnection() {
        for (AsyncTask current : threadList) {
            if (current.isCancelled())
                current.cancel(true);
        }

        s.close();
        multicastLock.release();
    }

    private void initMulticastSocket() {

        if(multicastLock == null) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("multicastLock");
        }

        if(s == null || s.isClosed()) {
            try {
                group = InetAddress.getByName(ipAdress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                s = new MulticastSocket(port);
                s.joinGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
