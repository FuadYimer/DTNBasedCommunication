package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;

import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;
import org.disrupted.ibits.util.Log;


import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.NetworkCoordinator;
import org.disrupted.ibits.network.linklayer.events.LinkLayerStarted;
import org.disrupted.ibits.network.linklayer.events.LinkLayerStopped;
import org.disrupted.ibits.network.linklayer.LinkLayerAdapter;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class BluetoothLinkLayerAdapter extends HandlerThread implements LinkLayerAdapter {

    private static final String TAG = "BTLinkLayerAdapter";

    public static final String LinkLayerIdentifier = "BLUETOOTH";

    private static BluetoothLinkLayerAdapter instance = null;
    private static final Object lock = new Object();

    private NetworkCoordinator networkCoordinator;
    private BluetoothScanner btScanner;
    private long started_time_nano;
    private boolean register;
    private boolean activated;

    public static BluetoothLinkLayerAdapter getInstance(NetworkCoordinator networkCoordinator) {
        synchronized (lock) {
            if(instance == null)
                instance = new BluetoothLinkLayerAdapter(networkCoordinator);
            return instance;
        }
    }

    private BluetoothLinkLayerAdapter(NetworkCoordinator networkCoordinator) {
        super(TAG);
        this.networkCoordinator = networkCoordinator;
        this.btScanner = BluetoothScanner.getInstance();
        register = false;
        activated = false;
        super.start();
    }

    @Override
    protected void finalize() throws Throwable {
        super.quit();
        super.finalize();
    }

    public String getLinkLayerIdentifier() {
        return LinkLayerIdentifier;
    }

    @Override
    public boolean isActivated() {
        return register;
    }

    public void linkStart() {
        if(register)
            return;
        register = true;
        Log.d(TAG, "[+] Starting Bluetooth");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);

        Handler handler = new Handler(getLooper());
        RumbleApplication.getContext().registerReceiver(mReceiver, filter, null, handler);

        if(BluetoothUtil.isEnabled())
            linkStarted();
    }

    public void linkStop() {
        if(!register)
            return;
        register = false;
        Log.d(TAG, "[-] Stopping Bluetooth");
        RumbleApplication.getContext().unregisterReceiver(mReceiver);

        linkStopped();
    }

    private void linkStarted() {
        if(activated)
            return;
        activated = true;
        Log.d(TAG, "[+] Bluetooth Activated");
        BluetoothUtil.prependRumblePrefixToDeviceName(RumbleProtocol.RUMBLE_BLUETOOTH_PREFIX);
        started_time_nano = System.nanoTime();
        btScanner.startScanner();
        networkCoordinator.addScanner(btScanner);
        EventBus.getDefault().post(new LinkLayerStarted(getLinkLayerIdentifier()));
    }

    private void linkStopped() {
        if(!activated)
            return;
        activated = false;
        Log.d(TAG, "[-] Bluetooth De-activated");
        EventBus.getDefault().post(new LinkLayerStopped(getLinkLayerIdentifier(),
                started_time_nano, System.nanoTime()));
        btScanner.stopScanner();
        networkCoordinator.delScanner(btScanner);
        BluetoothUtil.unprependRumblePrefixFromDeviceName(RumbleProtocol.RUMBLE_BLUETOOTH_PREFIX);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.d(TAG, "[!] BT State Changed");
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)){
                    case BluetoothAdapter.STATE_ON:
                        linkStarted();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        linkStopped();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }

            if(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED.equals(action)){
            }
        }
    };
}