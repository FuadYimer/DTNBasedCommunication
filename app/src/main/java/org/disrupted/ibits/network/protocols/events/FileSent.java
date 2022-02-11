package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.network.events.NetworkEvent;

import java.util.List;

/**
 * @author
 */
public class FileSent extends NetworkEvent {

    public String filepath;
    public List<String> recipients;
    public String protocolID;
    public String linkLayerIdentifier;

    public FileSent(String filepath, List<String> recipients, String protocolID, String linkLayerIdentifier) {
        this.filepath = filepath;
        this.recipients = recipients;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        return "filepath: "+filepath;
    }
}
