package com.github.otbproject.otbproject.proc;

public class ProcessedMessage {
    private static final ProcessedMessage EMPTY_INSTANCE = new ProcessedMessage("", "", false, new String[0], false);

    public final String response;
    public final String commandName;
    public final boolean isScript;
    public final String[] args;
    public final boolean timedOut;

    public ProcessedMessage(ProcessedCommand command, boolean timedOut) {
        this(command.response, command.commandName, command.isScript, command.args, timedOut);
    }

    public ProcessedMessage(String response, String commandName, boolean isScript, String[] args, boolean timedOut) {
        this.response = response;
        this.commandName = commandName;
        this.isScript = isScript;
        this.args = args;
        this.timedOut = timedOut;
    }

    public static ProcessedMessage empty() {
        return EMPTY_INSTANCE;
    }
}
