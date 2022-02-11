package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.linklayer.exception.LinkLayerConnectionException;
import org.disrupted.ibits.network.linklayer.exception.NoRemoteBluetoothDevice;
import org.disrupted.ibits.network.linklayer.exception.NullSocketException;

import java.io.IOException;

/**
 * @author
 */
public class BluetoothServerConnection extends BluetoothConnection {

    private static final String TAG = "BluetoothServerConnection";

    public BluetoothServerConnection(BluetoothSocket socket) {
        super(socket.getRemoteDevice().getAddress());
        this.mmConnectedSocket = socket;
    }

    @Override
    public String getConnectionID() {
        return "Bluetooth ServerConnection: " + remoteMacAddress;
    }

    @Override
    public void connect() throws LinkLayerConnectionException {

        if (mmConnectedSocket == null)
            throw new NullSocketException();

        this.mmBluetoothDevice = mmConnectedSocket.getRemoteDevice();

        if (mmBluetoothDevice == null)
            throw new NoRemoteBluetoothDevice();

        this.remoteMacAddress = mmBluetoothDevice.getAddress();

        try {
            inputStream = mmConnectedSocket.getInputStream();
            outputStream = mmConnectedSocket.getOutputStream();
        } catch (IOException e) {
            throw new InputOutputStreamException();
        }

        socketConnected = true;

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        RumbleApplication.getContext().registerReceiver(mReceiver, filter);
        registered = true;
    }

}
