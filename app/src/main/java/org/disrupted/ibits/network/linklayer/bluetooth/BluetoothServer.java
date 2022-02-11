package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import org.disrupted.ibits.util.Log;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.events.ScannerNeighbourSensed;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.network.Worker;

import java.io.IOException;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public abstract class BluetoothServer implements Worker {

    private static final String TAG = "BluetoothServer";

    protected String localMacAddress;
    protected BluetoothServerSocket mmServerSocket;
    protected UUID bt_service_uuid;
    protected String bt_service_name;
    protected boolean secureSocket;
    private boolean working;

    public BluetoothServer(UUID uuid, String name, boolean secure) {
        this.bt_service_uuid = uuid;
        this.bt_service_name = name;
        this.secureSocket = secure;
        this.working = false;
    }

    @Override
    public final String getLinkLayerIdentifier() {
        return BluetoothLinkLayerAdapter.LinkLayerIdentifier;
    }

    @Override
    public String getWorkerIdentifier() {
        return "BluetoothServer";
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public void cancelWorker() {
        if(working) {
            Log.d(TAG, "[!] should not call cancelWorker() on a working Worker, call stopWorker() instead !");
            stopWorker();
        }
    }

    @Override
    public void startWorker() {
        if(working)
            return;
        working = true;

        BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(RumbleApplication.getContext());
        if(adapter == null)
            return;

        localMacAddress = adapter.getAddress();
        BluetoothServerSocket tmp = null;

        try {
            if(secureSocket)
                tmp = adapter.listenUsingRfcommWithServiceRecord(this.bt_service_name, this.bt_service_uuid);
            else
                tmp = adapter.listenUsingInsecureRfcommWithServiceRecord(this.bt_service_name,this.bt_service_uuid);
        } catch (IOException e) {
            Log.d(TAG, "cannot open Listen Socket on service record "+bt_service_uuid);
            return;
        }

        mmServerSocket = tmp;
        if(tmp == null){
            Log.d(TAG, "cannot open Listen Socket on service record "+bt_service_uuid);
            return;
        }

        try {
            Log.d(TAG, "[+] bluetooth server started on UUID "+bt_service_uuid);
            while(true) {
                BluetoothSocket mmConnectedSocket = mmServerSocket.accept();
                if (mmConnectedSocket != null) {
                    Log.d(TAG, "[+] Client connected");

                    LinkLayerNeighbour neighbour = new BluetoothNeighbour(mmConnectedSocket.getRemoteDevice().getAddress());

                    onClientConnected(mmConnectedSocket);

                    EventBus.getDefault().post(new ScannerNeighbourSensed(neighbour));
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "[-] ENDED "+getWorkerIdentifier());
        } finally {
            stopWorker();
        }
    }

    abstract protected void onClientConnected(BluetoothSocket mmConnectedSocket);

    @Override
    public void stopWorker() {
        if(!working)
            return;
        working = false;

        try {
            mmServerSocket.close();
        } catch (Exception ignore) {
        }
    }
}

