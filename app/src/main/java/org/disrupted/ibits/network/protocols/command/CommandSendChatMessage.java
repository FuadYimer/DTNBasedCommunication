package org.disrupted.ibits.network.protocols.command;

import org.disrupted.ibits.database.objects.ChatMessage;

/**
 * @author
 */
public class CommandSendChatMessage extends Command {

    private ChatMessage chatMessage;

    public CommandSendChatMessage(ChatMessage chatMessage){
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    @Override
    public CommandID getCommandID() {
        return CommandID.SEND_CHAT_MESSAGE;
    }
}
