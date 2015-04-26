package com.github.otbproject.otbproject.messages.send;

public enum MessagePriority {
    LOW(2), DEFAULT(1), HIGH(0);

    private final int priority;

    MessagePriority(int priority) {
        this.priority = priority;
    }

    public int getValue() {
        return priority;
    }
}
