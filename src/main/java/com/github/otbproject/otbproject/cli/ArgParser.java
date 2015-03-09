package com.github.otbproject.otbproject.cli;

import org.apache.commons.cli.*;

public class ArgParser {
    public static class Opts {
        public static final String HELP = "help";
        public static final String HELP_SHORT = "h";
        public static final String BASE_DIR = "base-dir";
        public static final String ACCOUNT_FILE = "account-file";
        public static final String ACCOUNT = "account";
        public static final String OAUTH = "oauth";
        public static final String DEBUG = "debug";
    }

    public static CommandLine parse(String[] args) throws ParseException {
        Options options = getMainOptions();

        CommandLineParser parser = new GnuParser();

        return parser.parse(options, args);
    }

    private static Options getMainOptions() {
        Options options = new Options();

        options.addOption(Opts.HELP_SHORT, Opts.HELP, false, "Prints this help message and exits");

        // --base-dir
        OptionBuilder.withLongOpt(Opts.BASE_DIR);
        OptionBuilder.withDescription("The directory in which to find or create a '.otbproject' directory");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("PATH");
        Option baseDir = OptionBuilder.create();
        options.addOption(baseDir);

        // --account-file
        OptionBuilder.withLongOpt(Opts.ACCOUNT_FILE);
        OptionBuilder.withDescription("The name of the file in the config directory from which to load account information");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("ACCT_FILE_NAME");
        Option accountFile = OptionBuilder.create();
        options.addOption(accountFile);

        // --account
        OptionBuilder.withLongOpt(Opts.ACCOUNT);
        OptionBuilder.withDescription("The twitch account with which to log in. Overrides account name from --" + Opts.ACCOUNT_FILE);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("ACCT_NAME");
        Option account = OptionBuilder.create();
        options.addOption(account);

        // --oauth
        OptionBuilder.withLongOpt(Opts.OAUTH);
        OptionBuilder.withDescription("The oauth token with which to log in. Overrides oauth token from --" + Opts.ACCOUNT_FILE);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("TOKEN");
        Option oauth = OptionBuilder.create();
        options.addOption(oauth);

        // --debug
        OptionBuilder.withLongOpt(Opts.DEBUG);
        OptionBuilder.withDescription("Run in debug mode");
        Option debug = OptionBuilder.create();
        options.addOption(debug);

        return options;
    }

    public static void printHelp() {
        new HelpFormatter().printHelp("java -jar otbproject.jar [OPTIONS]", ArgParser.getMainOptions());
    }
}
