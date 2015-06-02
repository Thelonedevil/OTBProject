package com.github.otbproject.otbproject.commands.loader;

public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
    }

    public InvalidCommandException(String message) {
        super(message);
    }
}
