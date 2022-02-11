package org.disrupted.ibits.network.protocols.command;

/**
 * @author
 */
public class CommandSendKeepAlive extends Command {

    @Override
    public CommandID getCommandID() {
        return CommandID.SEND_KEEP_ALIVE;
    }
}
