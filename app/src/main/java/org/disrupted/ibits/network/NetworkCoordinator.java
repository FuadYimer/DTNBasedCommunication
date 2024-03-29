
package org.disrupted.ibits.network;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;
import org.disrupted.ibits.util.Log;

import org.disrupted.ibits.R;
import org.disrupted.ibits.network.services.ServiceLayer;
import org.disrupted.ibits.network.services.chat.ChatService;
import org.disrupted.ibits.network.services.push.PushService;
import org.disrupted.ibits.userinterface.activity.RoutingActivity;
import org.disrupted.ibits.network.linklayer.Scanner;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothLinkLayerAdapter;
import org.disrupted.ibits.network.linklayer.LinkLayerAdapter;
import org.disrupted.ibits.network.linklayer.wifi.WifiLinkLayerAdapter;
import org.disrupted.ibits.network.protocols.Protocol;
import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;


/**
 * NetworkCoordinator coordinate every network related events:
 *  - it starts/stops the LinkLayerAdapters (Bluetooth, Wifi)
 *  - it starts/stops the protocol stack (Rumble, Firechat, etc.)
 *  - it starts/stops the services (PushService, ChatService)
 *
 * @author
 */
public class NetworkCoordinator extends Service {

    public static final String ACTION_START_FOREGROUND = "org.disruptedsystems.ibits.action.startforeground";
    public static final String ACTION_STOP_NETWORKING  = "org.disruptedsystems.ibits.action.stopnetworking";
    public static final String ACTION_MAIN_ACTION      = "org.disruptedsystems.ibits.action.mainaction";
    public static final int    FOREGROUND_SERVICE_ID   = 4242;

    private static final String TAG = "NetworkCoordinator";
    private static final Object lock = new Object();

    private Looper  serviceLooper;
    private Handler serviceHandler;

    private List<LinkLayerAdapter>  adapters;
    private Map<String, WorkerPool> workerPools;
    private List<Protocol>          protocols;
    private List<ServiceLayer>      services;

    private List<Scanner>    scannerList;
    public  NeighbourManager neighbourManager;

    public boolean networkingStarted;

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public NetworkCoordinator getService() {
            return NetworkCoordinator.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (lock) {
            Log.d(TAG, "[+] Starting NetworkCoordinator");

            HandlerThread serviceThread = new HandlerThread("NetworkCoordinatorThread",
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
            serviceThread.start();
            serviceLooper = serviceThread.getLooper();
            serviceHandler = new Handler(serviceLooper);
            serviceHandler.post(new Runnable() {
                @Override
                public void run() {
                    neighbourManager = new NeighbourManager();
                    scannerList = new LinkedList<Scanner>();

                    // register link layers and their pool
                    adapters = new LinkedList<LinkLayerAdapter>();
                    workerPools = new HashMap<String, WorkerPool>();
                    BluetoothLinkLayerAdapter bluetoothLinkLayerAdapter =
                            BluetoothLinkLayerAdapter.getInstance(NetworkCoordinator.this);
                    adapters.add(bluetoothLinkLayerAdapter);
                    workerPools.put(BluetoothLinkLayerAdapter.LinkLayerIdentifier, new WorkerPool(5));
                    WifiLinkLayerAdapter wifiAdapter = new WifiLinkLayerAdapter();
                    adapters.add(wifiAdapter);
                    workerPools.put(wifiAdapter.getLinkLayerIdentifier(), new WorkerPool(10));

                    // register protocols
                    protocols = new LinkedList<Protocol>();
                    protocols.add(RumbleProtocol.getInstance(NetworkCoordinator.this));
                    //protocols.add(FirechatProtocol.getInstance(this));

                    // register services
                    services = new LinkedList<ServiceLayer>();
                    services.add(PushService.getInstance(NetworkCoordinator.this));
                    services.add(ChatService.getInstance(NetworkCoordinator.this));

                    networkingStarted = false;
                    EventBus.getDefault().register(NetworkCoordinator.this);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        synchronized (lock) {
            Log.d(TAG, "[-] Destroy NetworkCoordinator");

            serviceHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopNetworking();
                    services.clear();
                    services = null;
                    protocols.clear();
                    protocols = null;
                    workerPools.clear();
                    workerPools = null;
                    adapters.clear();
                    adapters = null;

                    if (EventBus.getDefault().isRegistered(NetworkCoordinator.this))
                        EventBus.getDefault().unregister(NetworkCoordinator.this);

                    System.exit(0);
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (lock) {
            if (intent == null)
                return START_STICKY;

            if (intent.getAction().equals(ACTION_START_FOREGROUND)) {
                Log.d(TAG, "Received Start Foreground Intent ");
                android.util.Log.d(TAG, "Received Start Foreground Intent ");
                serviceHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // start networking from a new thread to avoid performing network
                        // related operation from the UI thread
                        startNetworking();
                    }
                });
                android.util.Log.d(TAG, "NetworkCoordinator: onStartCommand()");
                Intent notificationIntent = new Intent(this, RoutingActivity.class);
                notificationIntent.setAction(ACTION_MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_IMMUTABLE  );

//                PendingIntent.getActivity(
//                        applicationContext,
//                        NOTIFICATION_REQUEST_CODE,
//                        updatedIntent,
//                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
//                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //startMyOwnForeground();
                    android.util.Log.d(TAG, "NetworkCoordinator: onStartCommand()   if statment ");
                }
                else {
                    android.util.Log.d(TAG, "NetworkCoordinator: onStartCommand()  Else Statment");
                    // channel id added by Fuad !
                    Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle("Rumble")
                            .setTicker("Rumble started")
                            .setContentText("Rumble started")
                            .setSmallIcon(R.drawable.small_icon)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true).build();
                    startForeground(FOREGROUND_SERVICE_ID, notification);
                }
            }


        }


        return START_STICKY;
    }


    public Looper getServiceLooper() {
        return serviceLooper;
    }

    public void startNetworking() {
        if(networkingStarted)
            return;
        networkingStarted = true;

        // start the services
        for (ServiceLayer service : services) {
            service.startService();
        }

        // start the protocol
        for (Protocol protocol : protocols) {
            protocol.protocolStart();
        }

        // start the link layers
        for (LinkLayerAdapter adapter : adapters) {
            WorkerPool pool = workerPools.get(adapter.getLinkLayerIdentifier());
            pool.startPool();
            adapter.linkStart();
        }
        neighbourManager.startMonitoring();
    }

    public void stopNetworking() {
        if(!networkingStarted)
            return;
        networkingStarted = false;
        neighbourManager.stopMonitoring();
        // stop services
        for(ServiceLayer service : services) {
            service.stopService();
        }
        // destroy protocols
        for (Protocol protocol : protocols) {
            protocol.protocolStop();
        }
        // destroy worker pools
        for (Map.Entry<String, WorkerPool> entry : workerPools.entrySet()) {
            entry.getValue().stopPool();
            entry.setValue(null);
        }
        // stop link layers
        for (LinkLayerAdapter adapter : adapters) {
            adapter.linkStop();
        }
    }

    public void linkLayerStart(String linkLayerIdentifier) {
        for (LinkLayerAdapter adapter : adapters) {
            if(adapter.getLinkLayerIdentifier().equals(linkLayerIdentifier)) {
                WorkerPool pool = workerPools.get(linkLayerIdentifier);
                pool.startPool();
                adapter.linkStart();
            }
        }
    }

    public void linkLayerStop(String linkLayerIdentifier) {
        for (LinkLayerAdapter adapter : adapters) {
            if(adapter.getLinkLayerIdentifier().equals(linkLayerIdentifier)) {
                // stop the pool allocated for this linklayer
                WorkerPool pool = workerPools.get(linkLayerIdentifier);
                pool.stopPool();
            }
        }
    }

    public boolean isLinkLayerEnabled(String linkLayerIdentifier) {
        synchronized (lock) {
            if (adapters == null)
                return false;
            for (LinkLayerAdapter adapter : adapters) {
                if(adapter.getLinkLayerIdentifier().equals(linkLayerIdentifier))
                    return adapter.isActivated();
            }
            return false;
        }
    }
    public boolean isScanning() {
        synchronized (lock) {
            for (Iterator<Scanner> it = scannerList.iterator(); it.hasNext(); ) {
                Scanner scanner = it.next();
                if(scanner.isScanning())
                    return true;
            }
            return false;
        }
    }
    public void    forceScan() {
        synchronized (lock) {
            for (Iterator<Scanner> it = scannerList.iterator(); it.hasNext(); ) {
                Scanner scanner = it.next();
                scanner.forceDiscovery();
            }
        }
    }

    public void addScanner(Scanner scanner) {
        for (Iterator<Scanner> it = scannerList.iterator(); it.hasNext(); ) {
            Scanner element = it.next();
            if(element == scanner)
                return;
        }
        scannerList.add(scanner);
    }

    public void delScanner(Scanner scanner) {
        for (Iterator<Scanner> it = scannerList.iterator(); it.hasNext(); ) {
            Scanner element = it.next();
            if(element == scanner) {
                it.remove();
                return;
            }
        }
    }

    public boolean addWorker(Worker worker) {
        synchronized (lock) {
            if(workerPools == null)
                return false;
            String linkLayerIdentifier = worker.getLinkLayerIdentifier();
            if(workerPools.get(linkLayerIdentifier) == null)
                return false;
            workerPools.get(linkLayerIdentifier).addWorker(worker);
                return true;
        }
    }

    public void stopWorkers(String linkLayerIdentifier, String protocolIdentifier) {
        synchronized (lock) {
            if(workerPools == null)
                return;

            WorkerPool pool = workerPools.get(linkLayerIdentifier);
            if(pool != null)
                pool.stopWorkers(protocolIdentifier);
        }
    }

    public void stopWorker(String linkLayerIdentifier, String workerID) {
        synchronized (lock) {
            if(workerPools == null)
                return;

            WorkerPool pool = workerPools.get(linkLayerIdentifier);
            if(pool != null)
                pool.stopWorker(workerID);
        }
    }

    /*
     * Just to avoid warning in logcat
     */
    public void onEvent(NoSubscriberEvent event) {
    }

}
