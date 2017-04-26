package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.TableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.Presenter.TablePresenter;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.ExpansionsAdapter;


public class CreateTableActivity extends AppCompatActivity {

    private ArrayList<CardExpansion> expansions;

    private ListView expansionList;
    private Button createTableButton;

    private TablePresenter tpresenter;
    private String username;

    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private InetAddress group;
    private List<AsyncTask> threadList = new ArrayList<>();

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;


    private void initMulticastSocket(){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);

        initMulticastSocket();

        SharedPreferences prefs = this.getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        username = prefs.getString("name", null);

        tpresenter = new TablePresenter(this);

        final EditText tableName = (EditText)findViewById(R.id.tablename);

        expansionList = (ListView) findViewById(R.id.expansion_list);
        expansions = CardHandler.getExpansions(this);
        expansionList.setAdapter(new ExpansionsAdapter(this, expansions));

        createTableButton = (Button) findViewById(R.id.btn_startTable);
        createTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableInfo table = tpresenter.createTable(tableName.getText().toString(), username);
                threadList.add(new TableMulticastSender().execute(s, group, table, port));

                try {
                    MulticastSocket s2;
                    s2 = new MulticastSocket(port);
                    s2.joinGroup(group);
                    threadList.add(new HostMulticastReceiver(multicastLock, s2, group).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<CardExpansion> exp = new ArrayList<CardExpansion>();
                for (CardExpansion e : expansions) {
                    if (e.isSelected()) {
                        exp.add(e);
                    }
                }

                Intent intent = new Intent(view.getContext(), HostTableActivity.class);
                intent.putExtra("THIS.TABLE", table);
                intent.putExtra("THIS.EXPANSIONS", exp);
                startActivity(intent);
            }
        });



    }

    public void clickNext(View view) {
        //just for testing
        setContentView(R.layout.activity_lobby);
    }

    public void clickJoin(View view) {
        //just for testing
        setContentView(R.layout.activity_player_list);
    }

    public void clickTableRule(View view) {
        //just for testing
        setContentView(R.layout.activity_create_table);
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
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeName:
                try{
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                }
                catch(Exception e) {

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);

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
