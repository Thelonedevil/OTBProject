package com.github.otbproject.otbproject.proc;

public class ProcessedMessage {
    private final String response;
    private final String commandName;
    private final boolean script;
    private final String[] args;
    private final boolean timedOut;

    public ProcessedMessage(ProcessedCommand command, boolean timedOut) {
        this(command.getResponse(), command.getCommandName(), command.isScript(), command.getArgs(), timedOut);
    }

    public ProcessedMessage(String response, String commandName, boolean script, String[] args, boolean timedOut) {
        this.response = response;
        this.commandName = commandName;
        this.script = script;
        this.args = args;
        this.timedOut = timedOut;
    }

    public String getResponse() {
        return response;
    }

    public String getCommandName() {
        return commandName;
    }

    public boolean isScript() {
        return script;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean wasTimedOut() {
        return timedOut;
    }
}
