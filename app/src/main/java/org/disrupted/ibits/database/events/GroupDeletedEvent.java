package org.disrupted.ibits.database.events;

/**
 * @author
 */
public class GroupDeletedEvent extends DatabaseEvent {

    public final String gid;

    public GroupDeletedEvent(String gid) {
        this.gid = gid;
    }

    @Override
    public String shortDescription() {
        return "gid="+gid;
    }
}
