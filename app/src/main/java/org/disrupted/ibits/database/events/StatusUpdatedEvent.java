package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.PushStatus;

/**
 * @author
 */
public class StatusUpdatedEvent extends StatusDatabaseEvent {

    public final PushStatus status;

    public StatusUpdatedEvent(PushStatus status){
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
