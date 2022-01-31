package org.disrupted.ibits.userinterface.events;

/**
 * @author
 */
public class UserDeleteGroup extends UserInteractionEvent {

    public String gid;

    public UserDeleteGroup(String gid) {
        this.gid = gid;
    }

    @Override
    public String shortDescription() {
        return "gid="+gid;
    }
}
