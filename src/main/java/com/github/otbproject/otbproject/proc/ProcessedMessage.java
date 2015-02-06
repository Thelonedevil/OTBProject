package com.github.otbproject.otbproject.proc;

public class ProcessedMessage {
    private final String response;
    private final String commandName;
    private final boolean timedOut;

    public ProcessedMessage(String response, String commandName, boolean timedOut) {
        this.response = response;
        this.commandName = commandName;
        this.timedOut = timedOut;
    }

    public String getResponse() {
        return response;
    }

    public String getCommandName() {
        return commandName;
    }

    public boolean wasTimedOut() {
        return timedOut;
    }
}
