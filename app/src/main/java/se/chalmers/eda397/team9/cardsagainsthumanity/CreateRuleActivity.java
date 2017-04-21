package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.ExpansionsAdapter;


public class CreateRuleActivity extends AppCompatActivity {
    private ArrayList<CardExpansion> expansions;
    private ListView expansionList;
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
        final Button startTableButton = (Button) findViewById(R.id.btn_startTable);
        expansionList = (ListView) findViewById(R.id.expansion_list);
        expansions = CardHandler.getExpansions(getApplicationContext());
        expansionList.setAdapter(new ExpansionsAdapter(this, expansions));
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CardExpansion> exp = new ArrayList<CardExpansion>();
                for (CardExpansion e : expansions) {
                    if (e.isSelected()) {
                        exp.add(e);
                    }
                }
            }
        });
    }

    public void clickNext(View view) {
        //just for testing
        setContentView(R.layout.activity_game);
    }

    public void clickJoin(View view) {
        //just for testing
        setContentView(R.layout.activity_player_list);
    }

    public void clickTableRule(View view) {
        //just for testing
        setContentView(R.layout.activity_create_rule);
    }

    //Main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeName:
                //Do something
                return true;
            case R.id.changeTable:
                //Do something
                return true;
            case R.id.blackList:
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
