package org.disrupted.ibits.userinterface.events;

/**
 * @author
 */
public class UserLeaveGroup extends UserInteractionEvent {

    public String gid;

    public UserLeaveGroup(String gid) {
        this.gid = gid;
    }

    @Override
    public String shortDescription() {
        return "gid="+gid;
    }
}