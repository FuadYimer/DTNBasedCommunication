package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.database.objects.PushStatus;
import org.disrupted.ibits.network.events.NetworkEvent;

import java.util.Set;

/**
 * This event holds every information known on a transmission that happened successfully. These
 * information includes:
 *
 * - The sent status (as it was sent)
 * - The receiver(s) (or an estimation of it in the case of Multicast IP)
 * - The protocol used to transmit this status (ibits, firechat)
 * - The link layer used (bluetooth, wifi)
 * - The size of the data transmitted (bytes)
 * - The duration of the transmission (ms)
 *
 * These information will be used by different component to update some informations :
 *  - The CacheManager to update its list and the neighbour's queue as well
 *  - The LinkLayerAdapte to update its internal metric that is used by getBestInterface
 *  - The FragmentStatusList to provide a visual feedback to the user
 *
 * @author
 */
public class PushStatusSent extends NetworkEvent {

    public PushStatus status;
    public Set<Contact> recipients;
    public String protocolID;
    public String linkLayerIdentifier;

    public PushStatusSent(PushStatus status, Set<Contact> recipients, String protocolID, String linkLayerIdentifier) {
        this.status = status;
        this.recipients = recipients;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        if(status != null)
            return status.getPost()+" ("+status.getAuthor()+")";
        else
            return "";
    }
}
