package org.disrupted.ibits.userinterface.events;


import org.disrupted.ibits.database.objects.PushStatus;

/**
 * @author
 */
public class UserReadStatus extends UserInteractionEvent {

    public PushStatus status;

    public UserReadStatus(PushStatus status) {
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