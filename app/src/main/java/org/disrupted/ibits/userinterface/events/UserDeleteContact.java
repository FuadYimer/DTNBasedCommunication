package org.disrupted.ibits.userinterface.events;

/**
 * @author
 */
public class UserDeleteContact extends UserInteractionEvent {

    public String uid;

    public UserDeleteContact(String uid) {
        this.uid = uid;
    }

    @Override
    public String shortDescription() {
        return "uid="+uid;
    }

}
