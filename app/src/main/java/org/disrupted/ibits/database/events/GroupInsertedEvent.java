package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.Group;

/**
 * @author
 */
public class GroupInsertedEvent extends DatabaseEvent {

    public final Group group;

    public GroupInsertedEvent(Group group) {
        this.group = group;
    }

    @Override
    public String shortDescription() {
        if(group != null)
            return group.getName()+" ("+group.getGid()+")";
        else
            return "";
    }
}
