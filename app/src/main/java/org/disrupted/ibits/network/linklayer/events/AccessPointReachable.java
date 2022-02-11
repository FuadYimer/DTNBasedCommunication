package org.disrupted.ibits.network.linklayer.events;

import android.net.wifi.ScanResult;

import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class AccessPointReachable extends NetworkEvent{
    public ScanResult ap;

    public AccessPointReachable(ScanResult ap) {
        this.ap = ap;
    }

    @Override
    public String shortDescription() {
        return ap.SSID;
    }
}
