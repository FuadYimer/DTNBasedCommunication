package org.disrupted.ibits.network.protocols.events;

import org.disrupted.ibits.database.objects.ChatMessage;
import org.disrupted.ibits.network.events.NetworkEvent;

/**
 * @author
 */
public class ChatMessageSent extends NetworkEvent {

    public ChatMessage chatMessage;
    public String protocolID;
    public String linkLayerIdentifier;

    public ChatMessageSent(ChatMessage chatMessage, String protocolID, String linkLayerIdentifier) {
        this.chatMessage = chatMessage;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return null;
    }
}
