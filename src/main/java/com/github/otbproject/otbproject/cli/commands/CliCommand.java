package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;

import java.util.function.Supplier;

public class CliCommand implements Supplier<String> {
    private String shortHelp;
    private String longHelp;
    private Supplier<String> supplier;

    private CliCommand(String shortHelp, String longHelp, Supplier<String> supplier) {
        this.shortHelp = shortHelp;
        this.longHelp = longHelp;
        this.supplier = supplier;
    }

    @Override
    public String get() {
        return supplier.get();
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
        private Supplier<String> supplier;

        public Builder() {
            init();
        }

        private void init() {
            shortHelp = "No short help message provided";
            longHelp = "No long help message provided";
            supplier = () -> {
                App.logger.warn("Missing supplier for cli command");
                return "";
            };
        }

        public Builder withShortHelp(String shortHelp) {
            this.shortHelp = shortHelp;
            return this;
        }

        public Builder withLongHelp(String longHelp) {
            this.longHelp = longHelp;
            return this;
        }

        public Builder withAction(Supplier<String> supplier) {
            this.supplier = supplier;
            return this;
        }

        public CliCommand create() {
            CliCommand cliCommand = new CliCommand(shortHelp, longHelp, supplier);
            init();
            return cliCommand;
        }
    }
}
