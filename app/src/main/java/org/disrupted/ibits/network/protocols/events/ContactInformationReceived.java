package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.network.events.NetworkEvent;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.network.protocols.ProtocolChannel;

/**
 * @author
 */
public class ContactInformationReceived extends NetworkEvent{

    public Contact contact;
    public int     flags;   // see class Contact
    public ProtocolChannel channel;
    public LinkLayerNeighbour neighbour;
    public boolean authenticated;

    public ContactInformationReceived(Contact contact, int flags, ProtocolChannel channel, LinkLayerNeighbour neighbour) {
        this.contact = contact;
        this.flags = flags;
        this.channel = channel;
        this.neighbour = neighbour;
        this.authenticated = false;
    }

    @Override
    public String shortDescription() {
        if(contact != null)
            return contact.getName()+" ("+contact.getUid()+")";
        else
            return "";
    }

}
