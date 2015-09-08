package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.beam.BeamBot;
import com.github.otbproject.otbproject.bot.irc.TwitchBot;
import com.github.otbproject.otbproject.bot.nullbot.NullBot;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.command.parser.CommandResponseParser;
import com.github.otbproject.otbproject.command.parser.TermLoader;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.filter.FilterProcessor;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.proc.CommandScriptProcessor;
import com.github.otbproject.otbproject.util.LibsLoader;
import com.github.otbproject.otbproject.util.ThreadUtil;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.preload.PreloadLoader;
import org.apache.commons.cli.CommandLine;

import java.awt.*;

public class Control {
    private static volatile boolean running = false;
    private static volatile Bot bot = NullBot.INSTANCE;

    public static Bot getBot() {
        return bot;
    }

    /**
     * Sets the bot to the given value. Fails if bot is running.
     *
     * @param bot A {@link Bot} to set as the bot
     * @return {@code true} if successfully set new value for bot
     */
    public static synchronized boolean setBot(Bot bot) {
        if (running) {
            return false;
        }
        Control.bot = bot;
        return true;
    }

    /**
     * Method used to start the bot the first time.
     * Performs some actions which only need to be done once. Should not be run more than once.
     *
     * @return {@code true} if the bot started successfully, {@code false} if it failed to start
     */
    public static synchronized boolean firstStartup() {
        LibsLoader.load();
        init();
        running = createBot();
        return running;
    }

    /**
     * Restarts the bot. Works whether or not bot is currently running.
     *
     * @return {@code true} if bot started successfully, {@code false} if it failed to start
     */
    public static synchronized boolean restart() {
        shutdown(true);
        try {
            return startup();
        } catch (StartupException e) {
            App.logger.catching(e);
            return false;
        }
    }

    /**
     * Does what it says on the tin
     */
    public static synchronized void shutdownAndExit() {
        shutdown(false);
        App.logger.info("Process stopped");
        System.exit(0);
    }

    /**
     * Stops the bot and cleans up anything which needs to be cleaned up
     * before the bot is started again
     *
     * @param cleanup whether or not to cleanup various data with the
     *                expectation that the bot will be started again
     */
    public static synchronized boolean shutdown(boolean cleanup) {
        if (!running) {
            return false;
        }

        App.logger.info("Shutting down bot");
        Bot oldBot = bot;
        bot = NullBot.INSTANCE; // Quickly make calls to getBot() return NullBot
        oldBot.shutdown();
        if (cleanup) {
            shutdownCleanup(oldBot);
        }
        running = false;
        return true;
    }

    private static void shutdownCleanup(Bot bot) {
        clearCaches();
        bot.getChannels().clear();
        // TODO unload libs?
    }

    /**
     * Should NOT be used for initial startup. Does not handle command line
     * args. The function startup(CommandLine) should instead be used to
     * start the bot the first time.
     * <p>
     * Should be run to start the bot after shutdown() has been called
     *
     * @return true if bot started successfully, false if bot was already running
     * @throws StartupException if failed to create bot properly
     */
    public static synchronized boolean startup() throws StartupException {
        if (running) {
            return false;
        }

        loadConfigs();
        CommandResponseParser.reRegisterTerms();
        init();
        running = createBot();
        if (!running) {
            shutdown(true);
            throw new StartupException("Failed to start bot");
        }
        return true;
    }

    private static void clearCaches() {
        CommandScriptProcessor.clearScriptCache();
        FilterProcessor.clearScriptCache();
    }

    private static boolean createBot() {
        // Connect to service
        try {
            switch (Configs.getFromGeneralConfig(GeneralConfig::getService)) {
                case TWITCH:
                    bot = new TwitchBot();
                    break;
                case BEAM:
                    bot = new BeamBot();
                    break;
            }
        } catch (BotInitException e) {
            App.logger.catching(e);
        }
        if (bot == NullBot.INSTANCE) {
            App.logger.error("Failed to start bot");
            return false;
        } else {
            Runnable botRunnable = new BotRunnable();
            ThreadUtil.getSingleThreadExecutor("Bot").execute(botRunnable);
            return true;
        }
    }

    private static void init() {
        loadPreloads(LoadStrategy.OVERWRITE);
        TermLoader.loadTerms();
        // TODO load filters (scripts)
    }

    public static void loadPreloads(LoadStrategy strategy) {
        // TODO stream over Base when filters are ready
        PreloadLoader.loadDirectory(Base.CMD, Chan.ALL, null, strategy);
        PreloadLoader.loadDirectory(Base.ALIAS, Chan.ALL, null, strategy);
        PreloadLoader.loadDirectory(Base.CMD, Chan.BOT, null, strategy);
        PreloadLoader.loadDirectory(Base.ALIAS, Chan.BOT, null, strategy);

        PreloadLoader.loadDirectoryForEachChannel(Base.CMD, strategy);
        PreloadLoader.loadDirectoryForEachChannel(Base.ALIAS, strategy);
    }

    private static void loadConfigs() {
        // General config
        GeneralConfig generalConfig = Configs.readGeneralConfig(); // Must be read first for service info
        App.configManager.setGeneralConfig(generalConfig);

        // Account config
        Account account = Configs.readAccount();
        App.configManager.setAccount(account);

        // Web Config
        WebConfig webConfig = Configs.readWebConfig();
        App.configManager.setWebConfig(webConfig);

        loadOtherConfigs();
    }

    public static void loadConfigs(CommandLine cmd) {
        // General config
        App.configManager.setGeneralConfig(Configs.readGeneralConfig()); // Must be read first for service info
        if (cmd.hasOption(ArgParser.Opts.SERVICE)) {
            String serviceName = cmd.getOptionValue(ArgParser.Opts.SERVICE).toUpperCase();
            try {
                Configs.editGeneralConfig(config -> config.setService(Service.valueOf(serviceName)));
            } catch (IllegalArgumentException e) {
                App.logger.fatal("Invalid service name: " + serviceName);
                ArgParser.printHelp();
                System.exit(1);
            }
        }

        // Account config
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT_FILE)) {
            Configs.setAccountFileName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT_FILE));
        }
        App.configManager.setAccount(Configs.readAccount());
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT)) {
            Configs.editAccount(account -> account.setName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT)));
        }
        if (cmd.hasOption(ArgParser.Opts.PASSKEY)) {
            Configs.editAccount(account -> account.setPasskey(cmd.getOptionValue(ArgParser.Opts.PASSKEY)));
        }

        // Web Config
        App.configManager.setWebConfig(Configs.readWebConfig());
        if (cmd.hasOption(ArgParser.Opts.WEB)) {
            Configs.editWebConfig(webConfig -> webConfig.setEnabled(Boolean.parseBoolean(cmd.getOptionValue(ArgParser.Opts.WEB))));
        }

        loadOtherConfigs();
    }

    private static void loadOtherConfigs() {
        // Bot config
        App.configManager.setBotConfig(Configs.readBotConfig());
    }

    public static class Graphics {
        private static boolean withGui = true;

        public static void useGui(boolean useGui) {
            withGui = useGui;
        }

        public static boolean present() {
            return withGui && !GraphicsEnvironment.isHeadless();
        }
    }

    public static class StartupException extends Exception {
        private StartupException(String message) {
            super(message);
        }
    }
}
