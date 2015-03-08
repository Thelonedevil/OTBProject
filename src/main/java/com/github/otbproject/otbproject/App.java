package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.api.Api;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.cli.commands.InvalidCLICommandException;
import com.github.otbproject.otbproject.commands.loader.FSCommandLoader;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.eventlistener.IrcListener;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.github.otbproject.otbproject.util.dev.DevHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Configuration;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by justin on 02/01/2015.
 */
public class App {
    private static HashSet<String> channels = new HashSet<>();
    static Listener listener = new IrcListener();
    public static CustomBot bot;
    public static final Logger logger = LogManager.getLogger();
    public static Thread botThread;
    public static Runnable botRunnable;

    public static void main(String[] args) {
        try {
            doMain(args);
        } catch (Throwable t) {
            try {
                System.err.println("A fatal problem has occurred.");
                t.printStackTrace();
                System.err.println("Attempting to log problem.");
                // TODO log throwable
            } finally {
                System.exit(-10);
            }
        }
    }

    public static void doMain(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = ArgParser.parse(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            ArgParser.printHelp();
            return;
        }

        if ((cmd == null) || cmd.hasOption(ArgParser.Opts.HELP)) {
            ArgParser.printHelp();
            return;
        }

        if (cmd.hasOption(ArgParser.Opts.BASE_DIR)) {
            String path = cmd.getOptionValue(ArgParser.Opts.BASE_DIR);
            if (new File(path).isDirectory()) {
                if (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }
                FSUtil.setBaseDirPath(path);
            } else {
                System.out.println("Error setting base directory.");
                System.out.println("The path:\t" + path);
                System.out.println("does not exist or is not a directory.");
                System.out.println();
                ArgParser.printHelp();
                return;
            }
        }

        System.setProperty("OTBCONF", FSUtil.logsDir());

        // Ensure directory tree is setup
        try {
            Setup.setup();
        } catch (IOException e) {
            App.logger.error("Unable to setup main directory tree at:\t" + FSUtil.getBaseDir());
            App.logger.catching(e);
            System.exit(1);
        }

        FSCommandLoader.LoadCommands();
        FSCommandLoader.LoadAliases();

        // TODO remove before release
        DevHelper.run(args);

        // Load account details
        String accountPath = FSUtil.configDir() + File.separator + "account.json";
        Account account = ConfigValidator.validateAccount(JsonHandler.readValue(accountPath, Account.class));
        JsonHandler.writeValue(accountPath, account);

        // Load general config
        String generalConfPath = FSUtil.configDir() + File.separator + "general-config.json";
        GeneralConfig generalConfig = ConfigValidator.validateGeneralConfig(JsonHandler.readValue(generalConfPath, GeneralConfig.class));
        JsonHandler.writeValue(generalConfPath, generalConfig);

        // Load bot config
        String botConfPath = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + "bot-config.json";
        BotConfig botConfig = ConfigValidator.validateBotConfig(JsonHandler.readValue(botConfPath, BotConfig.class));
        JsonHandler.writeValue(botConfPath, botConfig);
        channels = new HashSet<>(botConfig.currentChannels);

        //TODO get botname and oauth from config asell as possible server address and port
        Configuration.Builder configurationBuilder = new Configuration.Builder().setName(account.getName()).setAutoNickChange(false).setCapEnabled(false).addListener(listener).setServerHostname("irc.twitch.tv")
                .setServerPort(6667).setServerPassword(account.getOauth()).setEncoding(Charset.forName("UTF-8"));
        Configuration configuration = configurationBuilder.buildConfiguration();

        logger.info("Bot configuration built");
        bot = new CustomBot(configuration);

        // Store configs
        bot.configManager.setAccount(account);
        bot.configManager.setGeneralConfig(generalConfig);
        bot.configManager.setBotConfig(botConfig);
        botRunnable = new BotRunnable();
        botThread = new Thread(botRunnable);
        botThread.start();
        // Load channels
        for (String channelName : channels) {
            Api.joinChannel(channelName);
        }
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String in = scanner.nextLine();
            logger.info(in);
            String[] words = in.split(" ");
            try {
                CmdParser.SuperParse(words);
            } catch (InvalidCLICommandException e) {
                logger.info(e.getMessage());
                CmdParser.printHelp();
            }
        }
        scanner.close();
    }
    public static class BotRunnable implements Runnable{
        @Override
        public void run() {
            try {
                Thread.currentThread().setName("Main Bot");
                logger.info("Bot Started");
                bot.startBot();
                logger.info("Bot Stopped");
            } catch (IOException | IrcException e) {
                logger.catching(e);
            }

        }
    }

}
