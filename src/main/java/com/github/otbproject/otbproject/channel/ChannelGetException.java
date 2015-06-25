package com.github.otbproject.otbproject.channel;

/**
 * Checked Exception if unable to get a Channel
 */
public class ChannelGetException extends Exception {
    public ChannelGetException() {
        super();
    }

    public ChannelGetException(String message) {
        super(message);
    }
}
