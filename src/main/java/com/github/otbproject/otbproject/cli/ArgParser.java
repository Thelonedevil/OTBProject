package com.github.otbproject.otbproject.cli;

import org.apache.commons.cli.*;

public class ArgParser {
    public static class Opts {
        public static final String HELP = "help";
        public static final String HELP_SHORT = "h";
        public static final String BASE_DIR = "base-dir";
        public static final String BASE_DIR_SHORT = "d";
    }

    public static CommandLine parse(String[] args) throws ParseException {
        Options options = getMainOptions();

        CommandLineParser parser = new GnuParser();

        return parser.parse(options, args);
    }

    private static Options getMainOptions() {
        Options options = new Options();

        options.addOption(Opts.HELP_SHORT, Opts.HELP, false, "Prints this help message");
        options.addOption(Opts.BASE_DIR_SHORT, Opts.BASE_DIR, true, "The directory in which to find or create a '.otbproject' directory");

        return options;
    }

    public static void printHelp() {
        new HelpFormatter().printHelp("java -jar otbproject.jar [OPTIONS]", ArgParser.getMainOptions());
    }
}
