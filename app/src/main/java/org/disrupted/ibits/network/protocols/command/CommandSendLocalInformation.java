package org.disrupted.ibits.network.protocols.command;

import org.disrupted.ibits.database.objects.Contact;

/**
 * @author
 */
public class CommandSendLocalInformation extends Command {

    private Contact local;
    private int     flags;

    public CommandSendLocalInformation(Contact local, int flags){
        this.local = local;
        this.flags = flags;
    }

    public Contact getContact() {
        return local;
    }

    public int getFlags() {
        return flags;
    }

    @Override
    public CommandID getCommandID() {
        return CommandID.SEND_LOCAL_INFORMATION;
    }

}
