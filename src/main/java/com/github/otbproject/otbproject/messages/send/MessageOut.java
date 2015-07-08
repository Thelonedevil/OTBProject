package com.github.otbproject.otbproject.messages.send;

public class MessageOut {
    public final String message;
    public final MessagePriority priority;

    public MessageOut(String message) {
        this(message, MessagePriority.DEFAULT);
    }

    public MessageOut(String message, MessagePriority priority) {
        this.message = message;
        this.priority = priority;
    }
}
