package com.github.opentwitchbotteam.otbproject.messages.send;

public enum MessagePriority {
    LOW(0), DEFAULT(1), HIGH(2);

    private final int priority;

    MessagePriority(int priority) {
        this.priority = priority;
    }

    public int getValue() {
        return priority;
    }
}
