package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.disrupted.ibits.util.Log;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.network.linklayer.UnicastConnection;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.linklayer.exception.LinkLayerConnectionException;
import org.disrupted.ibits.network.linklayer.exception.NullSocketException;
import org.disrupted.ibits.network.linklayer.exception.SocketAlreadyClosedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * BluetoothConnection is a generic implementation of a Bluetooth Connection wether it is to manage
 * a client or to connect to a server. It is abstract as the connection part is specific and should
 * implements connect()  as requested per LinkLayerConnection interface.
 *
 * @author
 */
public abstract class BluetoothConnection implements UnicastConnection {

    private static final String TAG = "BluetoothConnection";

    protected String remoteMacAddress;
    protected boolean secureSocket;
    protected BluetoothDevice mmBluetoothDevice;
    protected BluetoothSocket mmConnectedSocket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected boolean registered;
    protected boolean socketConnected;

    public BluetoothConnection(String remoteMacAddress) {
        this.remoteMacAddress = remoteMacAddress;
        this.mmConnectedSocket = null;
        this.secureSocket = false;
        this.inputStream = null;
        this.outputStream = null;
        this.registered = false;
        this.socketConnected = false;
    }

    @Override
    public String getLinkLayerIdentifier() {
        return BluetoothLinkLayerAdapter.LinkLayerIdentifier;
    }

    @Override
    public int getLinkLayerPriority() {
        return LINK_LAYER_LOW_PRIORITY;
    }

    @Override
    public InputStream getInputStream() throws InputOutputStreamException{
        try {
            InputStream input = mmConnectedSocket.getInputStream();
            if (input == null)
                throw new InputOutputStreamException();
            return input;
        } catch (IOException e) {
            throw new InputOutputStreamException();
        }
    }

    @Override
    public String getRemoteLinkLayerAddress() {
        return remoteMacAddress;
    }

    @Override
    public LinkLayerNeighbour getLinkLayerNeighbour() {
        return new BluetoothNeighbour(getRemoteLinkLayerAddress());
    }

    @Override
    public OutputStream getOutputStream() throws InputOutputStreamException {
        try {
            OutputStream output = mmConnectedSocket.getOutputStream();
            if (output == null)
                throw new InputOutputStreamException();
            return output;
        } catch (IOException e) {
            throw new InputOutputStreamException();
        }
    }

    @Override
    public void disconnect() throws LinkLayerConnectionException {
        try {
            mmConnectedSocket.close();
        } catch (IOException e) {
            throw new SocketAlreadyClosedException();
        } catch (NullPointerException e) {
            throw new NullSocketException();
        } finally {
            try {
                if (registered)
                    RumbleApplication.getContext().unregisterReceiver(mReceiver);
                registered = false;
            } catch(Exception ignore) {
                Log.d(TAG, "[i] Receiver not registered ?");
            }
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if(device.getAddress().equals(mmBluetoothDevice.getAddress())) {
                    try {
                        Log.d(TAG, "[+] ACTION_ACL_DISCONNECTED");
                        disconnect();
                    } catch(LinkLayerConnectionException ignore) {
                    }
                }
            }
        }
    };


}
