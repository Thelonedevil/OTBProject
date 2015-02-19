package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.messages.send.MessagePriority;
import org.pircbotx.hooks.events.MessageEvent;

public class PackagedMessage {
    private String message;
    private String user;
    private String channel;
    private String destinationChannel;
    private boolean subscriber;
    private MessagePriority messagePriority;

    public PackagedMessage(String message, String user, String channel, String destinationChannel, boolean subscriber, MessagePriority messagePriority) {
        this.message = message;
        this.user = user;
        this.channel = channel;
        this.destinationChannel = destinationChannel;
        this.subscriber = subscriber;
        this.messagePriority = messagePriority;
    }

    public PackagedMessage(String message, String user, String channel, boolean subscriber, MessagePriority messagePriority) {
        this(message, user, channel, channel, subscriber, messagePriority);
    }

    public PackagedMessage(MessageEvent event) {
        // TODO replace false with actual subscriber info
        this(event.getMessage(), event.getUser().getNick(), event.getChannel().getName().replace("#", ""), false, MessagePriority.DEFAULT);
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
    }

    public String getDestinationChannel() {
        return destinationChannel;
    }

    public boolean isSubscriber() {
        return subscriber;
    }

    public MessagePriority getMessagePriority() {
        return messagePriority;
    }
}
