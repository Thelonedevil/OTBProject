package com.github.otbproject.otbproject.proc;

public class ProcessedCommand {
    private final String response;
    private final String commandName;

    public ProcessedCommand(String response, String commandName) {
        this.response = response;
        this.commandName = commandName;
    }

    public String getResponse() {
        return response;
    }

    public String getCommandName() {
        return commandName;
    }
}
