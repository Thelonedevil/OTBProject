package com.github.otbproject.otbproject.messages.send;

import java.util.Comparator;

public class MessageOut {
    public final String message;
    public final MessagePriority priority;
    private long insertionTime;

    public MessageOut(String message) {
        this(message, MessagePriority.DEFAULT);
    }

    public MessageOut(String message, MessagePriority priority) {
        this.message = message;
        this.priority = priority;
    }

    void recordInsertionTime() {
        insertionTime = System.currentTimeMillis();
    }

    public static final Comparator<MessageOut> PRIORITY_COMPARATOR =
            (o1, o2) -> (o1.priority.value == o2.priority.value) ?
                    Long.compare(o1.insertionTime, o2.insertionTime)
                    : Integer.compare(o1.priority.value, o2.priority.value);
}
