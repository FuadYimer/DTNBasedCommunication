package org.disrupted.ibits.network.protocols.command;

/**
 * @author
 */
public abstract class Command {

    public enum CommandID {
        SEND_PUSH_STATUS,
        SEND_LOCAL_INFORMATION,
        SEND_CHAT_MESSAGE,
        SEND_KEEP_ALIVE
    }

    abstract public CommandID getCommandID();

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o instanceof Command) {
            Command command = (Command) o;
            return getCommandID().equals(command.getCommandID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getCommandID().hashCode();
    }
}
