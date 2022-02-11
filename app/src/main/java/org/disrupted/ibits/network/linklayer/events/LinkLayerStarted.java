package org.disrupted.ibits.network.linklayer.events;

import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class LinkLayerStarted extends NetworkEvent {

    public String linkLayerIdentifier;
    public LinkLayerStarted(String linkLayerIdentifier) {
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        return linkLayerIdentifier;
    }
}
