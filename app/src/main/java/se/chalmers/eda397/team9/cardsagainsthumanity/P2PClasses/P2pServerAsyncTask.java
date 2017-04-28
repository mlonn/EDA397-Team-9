package se.chalmers.eda397.team9.cardsagainsthumanity.P2PClasses;

import android.content.Context;
import android.os.AsyncTask;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.R;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Serializer;


public class P2pServerAsyncTask extends AsyncTask {

    private Context context;
    private List<PlayerInfo> players;
    private PropertyChangeSupport pcs;
    private int port = 9888;

    public P2pServerAsyncTask(PlayerInfo host){
        players = new ArrayList<>();
        players.add(host);
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            ServerSocket serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();

            /* Reading the stream from the socket */
            int nRead;
            byte[] data = new byte[1024];
            InputStream inputStream = client.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            while((nRead = inputStream.read(data, 0, data.length)) != -1){
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            PlayerInfo newPlayerInfo = (PlayerInfo) Serializer.deserialize(buffer.toByteArray());
            pcs.firePropertyChange("TABLE_PLAYER_JOINED", null, newPlayerInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }
}
