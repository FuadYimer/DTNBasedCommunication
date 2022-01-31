package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.Contact;

/**
 * @author
 */
public class ContactUpdatedEvent extends StatusDatabaseEvent {

    public final Contact contact;

    public ContactUpdatedEvent(Contact contact){
        this.contact = contact;
    }

    @Override
    public String shortDescription() {
        if(contact != null)
            return contact.getName() + " ("+contact.getUid()+")";
        else
            return "";
    }
}
