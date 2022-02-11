package org.disrupted.ibits.network.protocols.command;

import org.disrupted.ibits.database.objects.PushStatus;

/**
 * @author
 */
public class CommandSendPushStatus extends Command {

    private PushStatus   status;

    public CommandSendPushStatus(PushStatus status){
        this.status = status;
    }

    public PushStatus getStatus() {
        return status;
    }

    @Override
    public CommandID getCommandID() {
        return CommandID.SEND_PUSH_STATUS;
    }

}
