package io.github.otbproject.otb.core.message.out;

public enum MessagePriority {
    LOW(0), DEFAULT(1), HIGH(2);

    private final int value;

    MessagePriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
