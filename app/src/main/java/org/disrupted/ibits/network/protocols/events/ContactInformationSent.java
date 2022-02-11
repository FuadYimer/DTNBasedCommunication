package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.network.events.NetworkEvent;
import org.disrupted.ibits.network.protocols.ProtocolChannel;

/**
 * @author
 */
public class ContactInformationSent extends NetworkEvent {

    public Contact contact;
    ProtocolChannel channel;

    public ContactInformationSent(Contact contact, ProtocolChannel channel) {
        this.contact = contact;
        this.channel = channel;
    }

    @Override
    public String shortDescription() {
        if(contact != null)
            return contact.getName()+" ("+contact.getUid()+")";
        else
            return "";
    }

}
