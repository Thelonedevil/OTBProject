package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.user.UserLevel;

public class PackagedMessage {
    public final String message;
    public final String user;
    public final String channel;
    public final String destinationChannel;
    public final UserLevel userLevel;
    public final MessagePriority messagePriority;

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
}
