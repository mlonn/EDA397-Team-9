package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by SAMSUNG on 2017-04-06.
 */

public abstract class MulticastReceiver<A, B, C> extends AsyncTask<A, B, C> {


    private WifiManager.MulticastLock mcLock;
    private MulticastSocket s;
    private InetAddress group;



    public MulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group){
        this.mcLock = mcLock;
        this.s = s;
        this.group = group;
        startMulticastLock();
    }

    public InetAddress getGroup(){
        return group;
    }

    public MulticastSocket getSocket(){
        return s;
    }

    private void startMulticastLock() {
        if (!mcLock.isHeld()) {
            mcLock.setReferenceCounted(true);
            mcLock.acquire();
        }
    }

    private void endMulticastLock() {
        if (mcLock != null && mcLock.isHeld()) {
            mcLock.release();
        }
    }

    @Override
    protected void onPostExecute(C result) {
        if (mcLock.isHeld())
            endMulticastLock();
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mcLock.isHeld())
            endMulticastLock();
    }
}
