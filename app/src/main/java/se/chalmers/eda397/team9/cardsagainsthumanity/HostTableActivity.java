package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerRowLayout;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class HostTableActivity extends AppCompatActivity{


    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;

    private ArrayList<CardExpansion> expansions;
    private ListView expansionList;

    private GridLayout playerGridLayout;
    private LinearLayout playerRow;

    String[] colorArray = {
            "#f8c82d", "#fbcf61", "#ff6f6f",
            "#e3a712", "#e5ba5a", "#d1404a",
            "#0dccc0", "#a8d164", "#3498db",
            "#0ead9a", "#27ae60", "#2980b9",
            "#d49e99", "#b23f73", "#48647c",
            "#74525f", "#832d51", "#2c3e50",
            "#e84b3a", "#fe7c60", "#ecf0f1",
            "#c0392b", "#404148", "#bdc3c7"};

    LinkedList<String> colorList;

    String ipAdress = "224.1.1.1";
    int port = 9879;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table);
        final Button startTableButton = (Button) findViewById(R.id.start_button);
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                startActivity(intent);
            }
        });
        colorList = new LinkedList<>(Arrays.asList(colorArray));

        threadList = new ArrayList<>();
        initMulticastSocket();

        playerGridLayout = (GridLayout) findViewById(R.id.playerlist_grid);

        addHostRow("Alex");
        addPlayerRow("Mikael");
        addPlayerRow("Axel");
        addPlayerRow("Alessandro");
        addPlayerRow("Emy");
        addPlayerRow("Mohannad");
        addPlayerRow("Debora");
        addPlayerRow("Karl");
        addPlayerRow("Gustav");
        addPlayerRow("Daniel");
        addPlayerRow("Debora");
        addPlayerRow("Karl");
        addPlayerRow("Gustav");
        addPlayerRow("Daniel");
        addPlayerRow("Debora");
        addPlayerRow("Karl");
        addPlayerRow("Gustav");
        addPlayerRow("Daniel");
        addPlayerRow("Debora");
        openConnection();

    }

    private void addHostRow(String name){
        PlayerRowLayout hostRow = new PlayerRowLayout(this);
        hostRow.setName(name);
        hostRow.setAsHost();

        int randomNumber = (int) (Math.random() * colorList.size());
        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);

        hostRow.setImageColor(color);
        playerGridLayout.addView(hostRow);
    }

    private void addPlayerRow(String name){
        int randomNumber = (int) (Math.random() * colorList.size());
        PlayerRowLayout playerRow = new PlayerRowLayout(this);
        playerRow.setName(name);

        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);

        playerRow.setImageColor(color);
        playerGridLayout.addView(playerRow);
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
    /* Main menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeName:
                //Do something

                //Example message (only for test)
                Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.changeTable:
                //Do something
                return true;
            case R.id.settings:
                //Do something
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
