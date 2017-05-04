package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class MulticastReceiver<A, B, C> extends AsyncTask<A, B, C> implements Serializable{


    private WifiManager.MulticastLock mcLock;
    private MulticastSocket s;
    private InetAddress group;
    private PropertyChangeSupport pcs;

    public MulticastReceiver(WifiManager.MulticastLock mcLock, MulticastSocket s, InetAddress group){
        this.mcLock = mcLock;
        this.s = s;
        this.group = group;
        pcs = new PropertyChangeSupport(this);
        startMulticastLock();
    }

    public InetAddress getGroup(){
        return group;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }

    protected PropertyChangeSupport getPropertyChangeSupport(){
        return pcs;
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
