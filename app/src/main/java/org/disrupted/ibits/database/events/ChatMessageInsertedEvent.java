package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.ChatMessage;
import org.disrupted.ibits.network.protocols.ProtocolChannel;

/**
 * @author
 */
public class ChatMessageInsertedEvent extends DatabaseEvent {

    public final ChatMessage chatMessage;
    public final ProtocolChannel channel;

    public ChatMessageInsertedEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        this.channel = null;
    }

    public ChatMessageInsertedEvent(ChatMessage chatMessage, ProtocolChannel channel) {
        this.chatMessage = chatMessage;
        this.channel = channel;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return "";
    }
}
