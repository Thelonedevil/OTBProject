package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.channel.Channel;

@FunctionalInterface
public interface MessageHandler {
    void onMessage(Channel channel, PackagedMessage packagedMessage, boolean timedOut);

    default MessageHandler andThen(MessageHandler after) {
        return (channel, packagedMessage, timedOut) -> {
            this.onMessage(channel, packagedMessage, timedOut);
            after.onMessage(channel, packagedMessage, timedOut);
        };
    }
}
