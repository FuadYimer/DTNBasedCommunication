package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.database.objects.ChatMessage;
import org.disrupted.ibits.network.events.NetworkEvent;
import org.disrupted.ibits.network.protocols.ProtocolChannel;

/**
 * @author
 */
public class ChatMessageReceived extends NetworkEvent {

    public ChatMessage chatMessage;
    public ProtocolChannel channel;

    public ChatMessageReceived(ChatMessage chatMessage, ProtocolChannel channel) {
        this.chatMessage = chatMessage;
        this.channel = channel;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return null;
    }

}
