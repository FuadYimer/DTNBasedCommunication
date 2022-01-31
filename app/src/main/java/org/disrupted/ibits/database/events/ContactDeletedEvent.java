package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.Contact;

/**
 * @author
 */
public class ContactDeletedEvent extends DatabaseEvent {

    public final Contact contact;

    public ContactDeletedEvent(Contact contact){
        this.contact = contact;
    }

    @Override
    public String shortDescription() {
        if(contact != null)
            return contact.getName();
        else
            return "";
    }

}
