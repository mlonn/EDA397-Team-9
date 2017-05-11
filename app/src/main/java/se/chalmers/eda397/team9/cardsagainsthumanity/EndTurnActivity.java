package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.GameState;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.GameMulticastReciever;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

public class EndTurnActivity extends AppCompatActivity implements PropertyChangeListener{
    private Game game;
    private TextView blackCardText;
    private TextView winner;
    private GameMulticastReciever gameMulticastReciever;
    /*MulticastSocket*/
    private InetAddress group;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;
    private PlayerInfo myPlayerInfo;
    private TableInfo myTableInfo;
    private String tableAddress;
    private String ipAdress;
    private int port;
    private int p2pPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_turn);
        game = (Game) getIntent().getExtras().get(IntentType.THIS_GAME);
        String str = (String) getIntent().getExtras().get(IntentType.WINNING_STRING);
        blackCardText = (TextView) findViewById(R.id.textviewWinningBlackCard);
        winner = (TextView) findViewById(R.id.winnerTextView);
        if (str == null) {
            blackCardText.setText("???");
        } else {
            blackCardText.setText(str);
        }
        if (game.getWinner() != null) {
            winner.setText(game.getWinner().getPlayer().getName());
        } else {
            winner.setText("???");
        }


        /*Multicast*/
        myTableInfo = (TableInfo) getIntent().getExtras().get(IntentType.THIS_TABLE);
        tableAddress = (String) getIntent().getStringExtra(IntentType.TABLE_ADDRESS);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(IndexActivity.GAME_SETTINGS_FILE, Context.MODE_PRIVATE);
        ipAdress = prefs.getString(IndexActivity.MULTICAST_IP_ADDRESS, null);
        port = prefs.getInt(IndexActivity.MULTICAST_PORT, 0);
        p2pPort = prefs.getInt(IndexActivity.P2P_PORT, 0);
        /* Initialize multicast */
        initMulticastSocket();
        myPlayerInfo = game.getPlayerByUUID(prefs.getString(IndexActivity.PLAYER_UUID, null));
        gameMulticastReciever = new GameMulticastReciever(multicastLock, s, group, myPlayerInfo, myTableInfo, "ENDTURNACTIVITY");
        gameMulticastReciever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (gameMulticastReciever != null){
            gameMulticastReciever.addPropertyChangeListener(this);
        }
    }

    public void updateWinner(final String cardText, final String winnerName){
        android.os.Handler handler = new android.os.Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                blackCardText.setText(cardText);
                winner.setText(winnerName);
            }
        });
    }
    private void initMulticastSocket() {
        if (multicastLock == null || !multicastLock.isHeld()) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("multicastLock");
        }

        if (s == null || s.isClosed()) {
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
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Message.Type.SELECTED_WINNER)){

            MulticastPackage msg = new MulticastPackage(tableAddress, Message.Response.RECEIVED_WINNER, myPlayerInfo);
            MulticastSender sender = new MulticastSender(msg, s, group);
            sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            GameState newGameState = (GameState) evt.getNewValue();
            game.setKing(newGameState.getNewKing());
            game.setBlackCard(newGameState.getBlackCard());
            game.setWinner(newGameState.getWinner());
            updateWinner(game.updateBlackCardText(game.getWinner().getWhiteCards()), game.getWinner().getPlayer().getName());
            /*    @Override
                public void run() {
                    Intent intent = new Intent(EndTurnActivity.this, GameActivity.class);
                    intent.putExtra(IntentType.TABLE_ADDRESS, myTableInfo.getHost().getDeviceAddress());
                    intent.putExtra(IntentType.THIS_GAME, game);
                    intent.putExtra(IntentType.THIS_TABLE, myTableInfo);
                    startActivity(intent);
                    finish();
                }
            },2000);*/
        }
    }
}
