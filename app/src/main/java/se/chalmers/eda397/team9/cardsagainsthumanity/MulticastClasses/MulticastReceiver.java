package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MulticastSocket;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.Serializer;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public abstract class MulticastReceiver<A, B, C> extends AsyncTask<A, B, C> {


    private WifiManager.MulticastLock mcLock;
    private MulticastSocket s;

    public MulticastReceiver(WifiManager.MulticastLock mcLock){
        this.mcLock = mcLock;
    }

    protected void startMulticastLock() {
        if (!mcLock.isHeld()) {
            mcLock.setReferenceCounted(true);
            mcLock.acquire();
        }
    }

    protected void endMulticastLock() {
        if (mcLock != null && mcLock.isHeld()) {
            mcLock.release();
        }
    }

    @Override
    protected void onPostExecute(C result) {
        if (mcLock.isHeld())
            endMulticastLock();

        if (!isCancelled())
            cancel(true);
    }
}
