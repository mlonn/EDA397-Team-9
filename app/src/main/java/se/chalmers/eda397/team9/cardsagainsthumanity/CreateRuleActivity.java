package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class CreateRuleActivity extends AppCompatActivity {

    private MulticastSocket s;
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rule);

        initMulticast();
        final Button closeTableButton = (Button) findViewById(R.id.closeTable_button);

        threadList = new ArrayList<>();

        closeTableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeConnection();
                Toast.makeText(CreateRuleActivity.this, "Table closed", Toast.LENGTH_SHORT).show();
            }
        });

        TableInfo table = (TableInfo) getIntent().getExtras().get("THIS.TABLE");

        threadList.add(new TableMulticastSender().execute(s, group, table, port));
        threadList.add(new HostMulticastReceiver(multicastLock, s, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table));

        Toast.makeText(this, "Table opened", Toast.LENGTH_SHORT).show();
    }

    private void closeConnection(){
        for(AsyncTask current : threadList){
            if(current.isCancelled())
                current.cancel(true);
        }
        try {
            s.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMulticast(){
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("multicastLock");

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
