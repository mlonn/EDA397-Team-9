package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.HostMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.ReliableMulticastSender;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.P2pManager;
import se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses.WiFiBroadcastReceiver;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerStatisticsFragment;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;

public class HostTableActivity extends AppCompatActivity implements PropertyChangeListener {

    /* Multicast variables */
    private InetAddress group;
    private List<AsyncTask> threadList;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket s;

    /* View variables*/
    private Button startTableButton;
    private Button closeTableButton;

    /* Color variables */
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

    //Temporary
    String ipAdress = "224.1.1.1";
    int port = 9879;

    /* Class variables */
    private ArrayList<CardExpansion> expansions;
    private PlayerInfo hostInfo;
    private TableInfo myTableInfo;
    private List<PlayerInfo> playerList;
    private List<PlayerInfo> connectedPlayers;
    private Integer maxConnectionRetries = 0;

    /* Fragment variables */
    private FragmentManager fragmentManager;
    private PlayerStatisticsFragment psFragment;

    /* P2P Variables */
    private WiFiBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private P2pManager p2pManager;
    private boolean receiverIsRegistered = false;
    private ArrayList<String> expansionsNames;
    private final int p2pPort = 9888;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table_new);
        /* Remove ? */
        expansions = (ArrayList<CardExpansion>) getIntent().getExtras().get(IntentType.THIS_EXPANSIONS);
        expansionsNames = (ArrayList<String>) getIntent().getExtras().get(IntentType.THIS_EXPANSION_NAMES);


        /* Initialize class variables */
        playerList = new ArrayList<>();
        colorList = new LinkedList<>(Arrays.asList(colorArray));
        p2pManager = new P2pManager(this);
        threadList = new ArrayList<>();
        connectedPlayers = new ArrayList<>();

        /* Initialize size of the ScrollView of the fragment*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        View scrollView = (View) findViewById(R.id.scroll_view);
        scrollView.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, (screenHeight - convertDpToPixels(150, getBaseContext()))));

        /* Initialize fragment variables */
        fragmentManager = getSupportFragmentManager();
        psFragment = (PlayerStatisticsFragment) fragmentManager.findFragmentById(R.id.playerFragment);

        /* Initialize views */
        startTableButton = (Button) findViewById(R.id.start_button);
        closeTableButton = (Button) findViewById(R.id.close_button);

        /* Initialize peer to peer and multicast socket */
        initP2p();
        initMulticastSocket();
        p2pManager.discoverPeers();

        /* Get table info */
        myTableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
        hostInfo = myTableInfo.getHost();
        addHost(hostInfo);


        /* View Listeners */
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MulticastPackage multicastPackage = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(), Message.Type.GAME_STARTED, expansionsNames);
                MulticastPackage expectedResponse = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(), Message.Response.GAME_START_CONFIRMED);
                HostMulticastSender sender = new HostMulticastSender(multicastPackage, expectedResponse, s, group, (ArrayList) myTableInfo.getPlayerList());
                sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                sender.addPropertyChangeListener(HostTableActivity.this);
            }
        });
        closeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseTableDialog();
            }
        });


    }

    private void connectToSocketAndTransferData(String host, Object objectToSend) {
        Context context = this.getApplicationContext();


        Socket socket = new Socket();
        byte buf[] = new byte[1024];

        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, p2pPort), 500);

            OutputStream outputStream = socket.getOutputStream();
            buf = Serializer.serialize(objectToSend);
            outputStream.write(buf);

            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void addHost(PlayerInfo hostInfo) {
        assignRandomColor(hostInfo);
        playerList.add(hostInfo);
        psFragment.addHost(hostInfo);
    }

    /* Adds player and into the table */
    private void addNewPlayer(PlayerInfo newPlayer) {
        assignRandomColor(newPlayer);
        myTableInfo.addPlayer(newPlayer);
        playerList.add(newPlayer);
        psFragment.addPlayer(newPlayer);
    }

    private void removePlayer(PlayerInfo player) {
        colorList.add(player.getColor());
        myTableInfo.removePlayer(player);
        playerList.remove(player);
        psFragment.removePlayer(player);
    }

    /* Initialize peer to peer */
    private void initP2p() {
        p2pManager = new P2pManager(this);
        mIntentFilter = p2pManager.getIntentFilter();
        receiver = p2pManager.getReceiver();
        receiver.addPropertyChangeListener(this);
        if (!receiverIsRegistered) {
            registerReceiver(receiver, mIntentFilter);
        }
    }

    private void assignRandomColor(PlayerInfo playerInfo) {
        int randomNumber = (int) (Math.random() * colorList.size());
        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);
        playerInfo.setColor(color);
    }

    /* Activiy overrides */

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        receiverIsRegistered = true;
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        receiverIsRegistered = false;
        super.unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiverIsRegistered)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMulticastSocket();
        if (!receiverIsRegistered) {
            registerReceiver(receiver, mIntentFilter);
        }
    }

    /* Method used for closing all async tasks and socket in this activity */
    //TODO: Concurrent modification exception
    private void closeConnection() {
        for (AsyncTask current : threadList) {
            if (!current.isCancelled())
                current.cancel(true);
        }

//        TODO: Find a way to close the socket safely
//        if(s != null || !s.isClosed())
//            s.close();
    }

    /* Method for initializing the multicast socket*/
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

    private void openCloseTableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to close this table?").setTitle("Table: " + myTableInfo.getName());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        openCloseTableDialog();
    }

    /* Main menu */
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

    /* Stops p2p connection and receiver */
    private void stopP2p() {
        p2pManager.stopDiscoverPeers();
        p2pManager.disconnect();
    }

    /* Sends package using MulticastSender*/
    private void sendPackage(final String target, final String type, final Serializable object) {
        Handler handler = new Handler(this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                MulticastPackage mPackage = new MulticastPackage(target,
                        type, object);
                threadList.add(new MulticastSender(mPackage, s, group).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            }
        });
    }

    private void sendReliablePackage(final MulticastPackage object, final MulticastPackage expectedResponse,
                                     final int maxRetries) {
        Handler handler = new Handler(this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                threadList.add(new ReliableMulticastSender(object, expectedResponse, maxRetries, s, group).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            }
        });
    }


    //TODO: Class starts to get big, might consider creating a handler class
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(Message.Response.ALL_CONFIRMED)) {
            p2pManager.discoverPeers();
            ArrayList<PlayerInfo> playerList = new ArrayList<PlayerInfo>();
            playerList.addAll(myTableInfo.getPlayerList());
            playerList.add(myTableInfo.getHost());
            Intent intent = new Intent(this, GameActivity.class);
            Game game = new Game(playerList, expansions);
            intent.putExtra(IntentType.THIS_GAME, game);
            startActivity(intent);
        }
        if (propertyChangeEvent.getPropertyName().equals(Message.Type.MY_DEVICE_ADDRESS_FOUND)) {
            String deviceAddress = (String) propertyChangeEvent.getNewValue();
            p2pManager.stopDiscoverPeers();

            TableInfo tableInfo = (TableInfo) getIntent().getSerializableExtra(IntentType.THIS_TABLE);
            tableInfo.getHost().setDeviceAddress(deviceAddress);

            HostMulticastReceiver hostMulticastReceiver = new HostMulticastReceiver(multicastLock, s, group, hostInfo);
            hostMulticastReceiver.addPropertyChangeListener(this);
            threadList.add(hostMulticastReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

            Toast.makeText(this, "Table opened", Toast.LENGTH_SHORT).show();
        }

        if (propertyChangeEvent.getPropertyName().equals(Message.Type.REQUEST_ALL_TABLES)) {
            sendPackage(Message.Target.ALL_DEVICES,
                    Message.Response.HOST_TABLE, myTableInfo);
            Log.d("HostTableActivity", "Sent table!");
        }

        if (propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_JOIN_REQUEST)) {
            final PlayerInfo newPlayer = (PlayerInfo) propertyChangeEvent.getNewValue();
            if (!myTableInfo.getPlayerList().contains(newPlayer)) {
                if (myTableInfo.getPlayerList().size() >= 19) {
                    sendPackage(newPlayer.getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_DENIED, null);

                } else if (findPlayer(playerList, newPlayer) != null) {
                    sendPackage(newPlayer.getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_DENIED, null);

                } else if (myTableInfo.getPlayerList().size() <= 19) {
                    //Updates view on the main UI thread
                    Handler mainHandler = new Handler(this.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addNewPlayer(newPlayer);

                        }
                    });
                    connectedPlayers.add(newPlayer);
                    //TODO: Testing reliable multicast sender

                    myTableInfo.addPlayer(newPlayer);

                    MulticastPackage mPackage = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_ACCEPTED, myTableInfo);
                    MulticastPackage expectedResponse = new MulticastPackage(myTableInfo.getHost().getDeviceAddress(),
                            Message.Response.PLAYER_JOIN_CONFIRM, null);
                    sendReliablePackage(mPackage, expectedResponse, 7);
                }
            }
        }

        if (propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_TIMED_OUT)) {
            removePlayer((PlayerInfo) propertyChangeEvent.getNewValue());
        }

        if (propertyChangeEvent.getPropertyName().equals(Message.Type.PLAYER_INTERVAL_UPDATE)) {
            PlayerInfo playerUpdate = (PlayerInfo) propertyChangeEvent.getNewValue();
            PlayerInfo player = findPlayer(playerList, playerUpdate);

            //Check if anything changed since last interval update of the player
            if (player == null) {
                sendPackage(player.getDeviceAddress(),
                        Message.Response.PLAYER_DISCONNECTED, null);
                return;
            }

            if (findPlayer(connectedPlayers, player) == null) {
                connectedPlayers.add(player);
            }
        }
    }

    /* Helper method to find player */
    private PlayerInfo findPlayer(List<PlayerInfo> list, PlayerInfo player) {
        for (PlayerInfo current : list) {
            if (current.getDeviceAddress() != null && current.getDeviceAddress().equals(player.getDeviceAddress()))
                return current;
        }
        return null;
    }

    public static int convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, resources.getDisplayMetrics());
        return (int) px;
    }

}
