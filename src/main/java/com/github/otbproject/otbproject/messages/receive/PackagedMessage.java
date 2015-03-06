package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;
import org.pircbotx.hooks.events.MessageEvent;

public class PackagedMessage {
    private String message;
    private String user;
    private String channel;
    private String destinationChannel;
    private UserLevel userLevel;
    private MessagePriority messagePriority;

    public PackagedMessage(String message, String user, String channel, String destinationChannel, UserLevel userLevel, MessagePriority messagePriority) {
        this.message = message;
        this.user = user;
        this.channel = channel;
        this.destinationChannel = destinationChannel;
        this.userLevel = userLevel;
        this.messagePriority = messagePriority;
    }

    public PackagedMessage(String message, String user, String channel, UserLevel userLevel, MessagePriority messagePriority) {
        this(message, user, channel, channel, userLevel, messagePriority);
    }

    public PackagedMessage(MessageEvent event) {
        this(event.getMessage(), event.getUser().getNick(), event.getChannel().getName().replace("#", ""), getUserLevelFromEvent(event), MessagePriority.DEFAULT);
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

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public MessagePriority getMessagePriority() {
        return messagePriority;
    }

    private static UserLevel getUserLevelFromEvent(MessageEvent event) {
        // TODO get correct UserLevel info from event and stuff
        return UserLevel.DEFAULT;
    }
}
