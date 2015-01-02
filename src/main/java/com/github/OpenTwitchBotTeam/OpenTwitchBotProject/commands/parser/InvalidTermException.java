package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.commands.parser;

public class InvalidTermException extends Exception {
    public InvalidTermException() {
        super("Invalid term.");
    }
    public InvalidTermException(String s) {
        super(s);
    }
}
