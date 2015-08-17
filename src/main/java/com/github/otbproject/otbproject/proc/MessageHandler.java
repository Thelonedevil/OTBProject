package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;

public interface MessageHandler {
    void onMessage(Channel channel, PackagedMessage packagedMessage, boolean timedOut);

    default MessageHandler andThen(MessageHandler messageHandler) {
        return (channel, packagedMessage, timedOut) -> {
            this.onMessage(channel, packagedMessage, timedOut);
            messageHandler.onMessage(channel, packagedMessage, timedOut);
        };
    }
}
