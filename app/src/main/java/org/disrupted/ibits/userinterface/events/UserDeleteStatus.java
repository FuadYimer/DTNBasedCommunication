package org.disrupted.ibits.userinterface.events;

import org.disrupted.ibits.database.objects.PushStatus;

/**
 * @author
 */
public class UserDeleteStatus extends UserInteractionEvent {

    public PushStatus status;

    public UserDeleteStatus(PushStatus status) {
        this.status = status;
    }

    @Override
    public String shortDescription() {
        if(status != null)
            return status.getPost()+" ("+status.getAuthor().getName()+")";
        else
            return "";
    }
}