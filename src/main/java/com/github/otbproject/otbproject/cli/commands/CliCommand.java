package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;

public class CliCommand implements Runnable {
    private String shortHelp;
    private String longHelp;
    private Runnable runnable;

    private CliCommand(String shortHelp, String longHelp, Runnable runnable) {
        this.shortHelp = shortHelp;
        this.longHelp = longHelp;
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
    }

    public String getLongHelp() {
        return longHelp;
    }

    public String getShortHelp() {
        return shortHelp;
    }

    public String getFullHelp() {
        return shortHelp + "\n" + longHelp;
    }

    public static class Builder {

        private String shortHelp;
        private String longHelp;
        private Runnable runnable;

        public Builder() {
            init();
        }

        private void init() {
            shortHelp = "No short help message provided";
            longHelp = "No long help message provided";
            runnable = () -> App.logger.warn("Missing runnable for cli command");
        }

        public Builder withShortHelp(String shortHelp) {
            this.shortHelp = shortHelp;
            return this;
        }

        public Builder withLongHelp(String longHelp) {
            this.longHelp = longHelp;
            return this;
        }

        public Builder withAction(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public CliCommand create() {
            CliCommand cliCommand = new CliCommand(shortHelp, longHelp, runnable);
            init();
            return cliCommand;
        }
    }
}
