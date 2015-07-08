package com.github.otbproject.otbproject.messages.send;

public enum MessagePriority {
    LOW(0), DEFAULT(1), HIGH(2);

    public final int value;

    MessagePriority(int value) {
        this.value = value;
    }
}
