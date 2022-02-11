package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class FileReceived extends NetworkEvent {

    public String filename;
    public String uuid;
    public String protocolID;
    public String linkLayerIdentifier;

    public FileReceived(String filename, String uuid, String protocolID, String linkLayerIdentifier) {
        this.filename = filename;
        this.uuid = uuid;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        return "filename:"+filename;
    }
}
