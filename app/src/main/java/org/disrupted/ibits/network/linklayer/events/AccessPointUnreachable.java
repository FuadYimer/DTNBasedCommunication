package org.disrupted.ibits.network.linklayer.events;

import android.net.wifi.ScanResult;

import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class AccessPointUnreachable extends NetworkEvent {

    public ScanResult ap;

    public AccessPointUnreachable(ScanResult ap) {
        this.ap = ap;
    }

    @Override
    public String shortDescription() {
        return ap.SSID;
    }
}
