package com.github.otbproject.otbproject.proc;

public class ProcessedCommand {
    private final String response;
    private final String commandName;
    private final boolean script;
    private final String[] args;

    public ProcessedCommand(String response, String commandName, boolean script, String[] args) {
        this.response = response;
        this.commandName = commandName;
        this.script = script;
        this.args = args;
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
}
