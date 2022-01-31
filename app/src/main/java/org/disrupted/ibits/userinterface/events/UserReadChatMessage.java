package org.disrupted.ibits.userinterface.events;

import org.disrupted.ibits.database.objects.ChatMessage;

/**
 * @author
 */
public class UserReadChatMessage extends UserInteractionEvent {

    public ChatMessage chatMessage;

    public UserReadChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return null;
    }
}
