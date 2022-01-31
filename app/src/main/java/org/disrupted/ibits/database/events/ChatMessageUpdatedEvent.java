package org.disrupted.ibits.database.events;

import org.disrupted.ibits.database.objects.ChatMessage;

/**
 * @author
 */
public class ChatMessageUpdatedEvent extends DatabaseEvent {

    public final ChatMessage chatMessage;

    public ChatMessageUpdatedEvent(ChatMessage chatMessage){
        this.chatMessage = chatMessage;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return "";
    }

}
