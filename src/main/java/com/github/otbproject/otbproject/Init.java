package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.bot.BotInitException;
import com.github.otbproject.otbproject.bot.BotRunnable;
import com.github.otbproject.otbproject.bot.beam.BeamBot;
import com.github.otbproject.otbproject.bot.irc.IRCBot;
import com.github.otbproject.otbproject.bot.irc.InputParserImproved;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.command.parser.CommandResponseParser;
import com.github.otbproject.otbproject.command.parser.TermLoader;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.util.LibsLoader;
import com.github.otbproject.otbproject.util.Util;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.preload.PreloadLoader;
import org.apache.commons.cli.CommandLine;

import java.lang.reflect.Field;

public class Init {

    static void startup(CommandLine cmd) {
        loadConfigs(cmd);
        LibsLoader.load();
        TermLoader.loadTerms();
        init();
        createBot();
    }

    public static void restart() {
        Bot.getBot().shutdown();
        restartInit();
    }

    private static void restartInit() {
        loadConfigs();
        CommandResponseParser.reRegisterTerms();
        TermLoader.loadTerms();
        init();
        createBot();
    }

    private static void createBot() {
        // Connect to service
        try {
            switch (Configs.getGeneralConfig().getServiceName()){
                case TWITCH:
                    Bot.setBot(new IRCBot());
                    Class c = Bot.getBot().getClass().getSuperclass();
                    Field input = c.getDeclaredField("inputParser");
                    input.setAccessible(true);
                    input.set(Bot.getBot(), new InputParserImproved((IRCBot) Bot.getBot()));
                    break;
                case BEAM:
                    Bot.setBot(new BeamBot());
                    break;
            }
        } catch (BotInitException | NoSuchFieldException | IllegalAccessException e) {
            App.logger.catching(e);
        }
        if (Bot.getBot() == null) {
            App.logger.error("Failed to start bot");
        } else {
            Bot.setBotRunnable(new BotRunnable());
            Bot.setBotFuture(Util.getSingleThreadExecutor("Bot").submit(Bot.getBotRunnable()));
        }
    }

    private static void init() {
        loadPreloads(LoadStrategy.OVERWRITE);
    }

    static void loadPreloads(LoadStrategy strategy) {
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

        // Bot config
        BotConfig botConfig = Configs.readBotConfig();
        App.configManager.setBotConfig(botConfig);
    }

    private static void loadConfigs(CommandLine cmd) {
        // General config
        GeneralConfig generalConfig = Configs.readGeneralConfig(); // Must be read first for service info
        App.configManager.setGeneralConfig(generalConfig);
        if (cmd.hasOption(ArgParser.Opts.SERVICE)) {
            String serviceName = cmd.getOptionValue(ArgParser.Opts.SERVICE).toUpperCase();
            if (serviceName.equals(ServiceName.TWITCH.toString())) {
                Configs.getGeneralConfig().setServiceName(ServiceName.TWITCH);
            } else if (serviceName.equals(ServiceName.BEAM.toString())) {
                Configs.getGeneralConfig().setServiceName(ServiceName.BEAM);
            } else {
                App.logger.error("Invalid service name: " + serviceName);
                ArgParser.printHelp();
                System.exit(1);
            }
            Configs.writeGeneralConfig();
        }

        // Account config
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT_FILE)) {
            Configs.setAccountFileName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT_FILE));
        }
        Account account = Configs.readAccount();
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT)) {
            account.setName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT));
        }
        if (cmd.hasOption(ArgParser.Opts.PASSKEY)) {
            account.setPasskey(cmd.getOptionValue(ArgParser.Opts.PASSKEY));
        }
        App.configManager.setAccount(account);
        Configs.writeAccount();

        // Bot config
        BotConfig botConfig = Configs.readBotConfig();
        App.configManager.setBotConfig(botConfig);
    }
}
