package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.Contact;

/**
 * @author
 */
public class ContactGroupListUpdated extends DatabaseEvent{

    public final Contact contact;

    public ContactGroupListUpdated(Contact contact) {
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
