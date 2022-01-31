package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.network.protocols.ProtocolChannel;

/**
 * @author
 */
public class ContactInterfaceInserted extends DatabaseEvent {

    public final Contact            contact;
    public final LinkLayerNeighbour neighbour;
    public final ProtocolChannel    channel;

    public ContactInterfaceInserted(Contact contact, LinkLayerNeighbour neighbour, ProtocolChannel channel){
        this.contact   = contact;
        this.neighbour = neighbour;
        this.channel   = channel;
    }

    @Override
    public String shortDescription() {
        if((contact != null) && (neighbour != null))
            return contact.getName() + " ("+contact.getUid()+") -> " +neighbour.getLinkLayerAddress() + "("+channel.getLinkLayerIdentifier()+")" ;
        else
            return "";
    }
}
