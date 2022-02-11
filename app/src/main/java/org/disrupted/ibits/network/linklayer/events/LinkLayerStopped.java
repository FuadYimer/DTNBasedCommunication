package org.disrupted.ibits.network.linklayer.events;

import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class LinkLayerStopped extends NetworkEvent {

    public String linkLayerIdentifier;
    public long   started_time_nano;
    public long   stopped_time_nano;


    public LinkLayerStopped(String linkLayerIdentifier, long started_time_nano, long stopped_time_nano) {
        this.linkLayerIdentifier = linkLayerIdentifier;
        this.started_time_nano = started_time_nano;
        this.stopped_time_nano = stopped_time_nano;
    }

    @Override
    public String shortDescription() {
        return linkLayerIdentifier;
    }
}
