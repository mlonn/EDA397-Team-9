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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.ExpansionsAdapter;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;


public class CreateTableActivity extends AppCompatActivity {

    private ArrayList<CardExpansion> expansions;

    private Button createTableButton;

    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private InetAddress group;
    private List<AsyncTask> threadList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText tableNameText = (EditText)findViewById(R.id.tablename);

        ListView expansionList = (ListView) findViewById(R.id.expansion_list);
        expansions = CardHandler.getExpansions(this);
        expansionList.setAdapter(new ExpansionsAdapter(this, expansions));

        createTableButton = (Button) findViewById(R.id.btn_startTable);
        createTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo myPlayerInfo = (PlayerInfo) getIntent().getSerializableExtra("PLAYER_INFO");
                TableInfo tableInfo = new TableInfo(tableNameText.getText().toString(), myPlayerInfo);

                ArrayList<CardExpansion> exp = new ArrayList<CardExpansion>();
                for (CardExpansion e : expansions) {
                    if (e.isSelected())
                        exp.add(e);
                }

                Intent intent = new Intent(view.getContext(), HostTableActivity.class);

                intent.putExtra(IntentType.THIS_TABLE, tableInfo);
                intent.putExtra(IntentType.THIS_EXPANSIONS, exp);

                if (exp.size()>0) {
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), "You Must Select At Least 1 Expansion", Toast.LENGTH_SHORT).show();

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
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("usernameFile", Context.MODE_PRIVATE);
        String username = prefs.getString("name", null);
        menu.findItem(R.id.profile).setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings:
                //Do something
                return true;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! I'm playing this wonderful game called King of Cards. Please download it you too from Play store so we can play together!";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "King of Cards");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
