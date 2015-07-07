package com.github.otbproject.otbproject.messages.send;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class MessageOut implements Comparable<MessageOut> {
    private final String message;
    private final MessagePriority priority;

    public MessageOut(String message) {
        this(message, MessagePriority.DEFAULT);
    }

    public MessageOut(String message, MessagePriority priority) {
        this.message = message;
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    @Override
    public int compareTo(MessageOut messageOut) {
        return Integer.compare(priority.getValue(), messageOut.priority.getValue());
    }
}
