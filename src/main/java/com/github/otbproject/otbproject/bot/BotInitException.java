package com.github.otbproject.otbproject.bot;

public class BotInitException extends Exception {
    private static final String MESSAGE = "Unable to properly create bot";

    public BotInitException() {
        super(MESSAGE);
    }

    public BotInitException(String message) {
        super(message);
    }

    public BotInitException(Throwable cause) {
        super(MESSAGE, cause);
    }

    public BotInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
