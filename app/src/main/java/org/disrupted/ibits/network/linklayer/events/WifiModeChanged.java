package org.disrupted.ibits.network.linklayer.events;

import org.disrupted.ibits.network.events.NetworkEvent;
import org.disrupted.ibits.network.linklayer.wifi.WifiLinkLayerAdapter;

/**
 * @author
 */
public class WifiModeChanged extends NetworkEvent {

    public WifiLinkLayerAdapter.WIFIMODE mode;

    public WifiModeChanged(WifiLinkLayerAdapter.WIFIMODE mode) {
        this.mode = mode;
    }

    @Override
    public String shortDescription() {
        return "";
    }
}
