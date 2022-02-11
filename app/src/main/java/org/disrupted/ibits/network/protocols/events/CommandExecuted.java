package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.network.events.NetworkEvent;
import org.disrupted.ibits.network.protocols.ProtocolChannel;
import org.disrupted.ibits.network.protocols.command.Command;

/**
 * @author
 */
public class CommandExecuted extends NetworkEvent {

    public ProtocolChannel worker;
    public Command command;
    public boolean success;

    public CommandExecuted(ProtocolChannel worker, Command command, boolean success) {
        this.worker = worker;
        this.command = command;
        this.success = success;
    }

    @Override
    public String shortDescription() {
        if((worker != null) && (command != null))
            return worker.getWorkerIdentifier()+" ("+command.getClass().getSimpleName()+")";
        else
            return "";
    }
}
