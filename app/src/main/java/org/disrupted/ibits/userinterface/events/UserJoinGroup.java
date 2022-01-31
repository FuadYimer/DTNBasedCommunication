package org.disrupted.ibits.userinterface.events;

import org.disrupted.ibits.database.objects.Group;

/**
 * @author
 */
public class UserJoinGroup extends UserInteractionEvent {
    public Group group;

    public UserJoinGroup(Group group) {
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
