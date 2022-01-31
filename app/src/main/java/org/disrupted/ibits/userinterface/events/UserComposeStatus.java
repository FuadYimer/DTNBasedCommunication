package org.disrupted.ibits.userinterface.events;

import org.disrupted.ibits.database.objects.PushStatus;

/**
 * @author
 */
public class UserComposeStatus extends UserInteractionEvent  {

    public final PushStatus status;
    public final String tempfile;

    public UserComposeStatus(PushStatus status) {
        this.status = status;
        this.tempfile = "";
    }


    public UserComposeStatus(PushStatus status, String tempfilename) {
        this.status = status;
        this.tempfile = (tempfilename != null) ? tempfilename : "";
    }

    @Override
    public String shortDescription() {
        if(status != null)
            return status.getPost()+" ("+status.getAuthor().getName()+")";
        else
            return "";
    }
}
