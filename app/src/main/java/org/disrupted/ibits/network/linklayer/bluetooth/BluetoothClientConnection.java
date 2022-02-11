package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.SystemClock;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.linklayer.events.BluetoothScanEnded;
import org.disrupted.ibits.network.linklayer.exception.ConnectionFailedException;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.linklayer.exception.InterruptedLinkLayerConnection;
import org.disrupted.ibits.network.linklayer.exception.LinkLayerConnectionException;
import org.disrupted.ibits.network.linklayer.exception.NoRemoteBluetoothDevice;
import org.disrupted.ibits.network.linklayer.exception.NullSocketException;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * BluetoothClient tries to establish a connection with a remote Bluetooth Device
 *
 * @author
 */
public class BluetoothClientConnection extends BluetoothConnection {

    private static final String TAG = "BluetoothClient";

    protected UUID   bt_service_uuid;
    protected String bt_service_name;

    private final CountDownLatch latch = new CountDownLatch(1);

    public BluetoothClientConnection(String remoteMacAddress, UUID uuid, String name, boolean secure){
        super(remoteMacAddress);
        this.bt_service_uuid = uuid;
        this.bt_service_name = name;
        this.secureSocket = secure;
        this.registered = false;
    }

    @Override
    public String getConnectionID() {
        return "Bluetooth ClientConnection: "+remoteMacAddress;
    }


    /*
     * If the BluetoothAdapter is performing a scan procedure, it should wait for it to finish
     * in order to avoid connection issue.
     */
    public void waitScannerToStop() throws LinkLayerConnectionException{
        if (BluetoothUtil.getBluetoothAdapter(RumbleApplication.getContext()).isDiscovering()) {
            EventBus.getDefault().register(this);
            try {
                latch.await();
                Random random = new Random();
                SystemClock.sleep(random.nextInt(2) * 1000);
            } catch(InterruptedException e) {
                throw new InterruptedLinkLayerConnection();
            }
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    public void connect() throws LinkLayerConnectionException {
        mmBluetoothDevice = BluetoothUtil.getBluetoothAdapter(RumbleApplication.getContext()).getRemoteDevice(this.remoteMacAddress);
        if (mmBluetoothDevice == null) throw new NoRemoteBluetoothDevice();

        try {
            if (secureSocket)
                mmConnectedSocket = mmBluetoothDevice.createRfcommSocketToServiceRecord(bt_service_uuid);
            else
                mmConnectedSocket = mmBluetoothDevice.createInsecureRfcommSocketToServiceRecord(bt_service_uuid);

            if (mmConnectedSocket == null)
                throw new NullSocketException();

            mmConnectedSocket.connect();
            socketConnected = true;
        }catch (IOException e) {
            throw new ConnectionFailedException(remoteMacAddress+" "+bt_service_uuid.toString());
        }

        try {
            inputStream = mmConnectedSocket.getInputStream();
            outputStream = mmConnectedSocket.getOutputStream();
        } catch (IOException e) {
            throw new InputOutputStreamException();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        RumbleApplication.getContext().registerReceiver(mReceiver, filter);
        registered = true;
    }

    /*
     * todo: it is possible that if the user stops everything it may keep a locking state
     * we don't want to connect while we are discovering cause it mess with the bluetooth
     * This one unlock the latch locked when trying to connect
     */
    public void onEvent(BluetoothScanEnded scanEnded) {
        latch.countDown();
    }


}
