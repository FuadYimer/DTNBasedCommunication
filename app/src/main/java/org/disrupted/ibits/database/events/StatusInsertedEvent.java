package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.PushStatus;

/**
 * NewStatusEvent is posted when a status has been added to the database
 * This must must ONLY be post by the StatusDatabase as the piggybacked message
 * should carry its database status ID.
 *
 * @author
 */
public class StatusInsertedEvent extends StatusDatabaseEvent {

    public final PushStatus status;

    public StatusInsertedEvent(PushStatus status){
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