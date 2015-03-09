package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.cli.commands.InvalidCLICommandException;
import com.github.otbproject.otbproject.commands.loader.FSCommandLoader;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.eventlistener.IrcListener;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.gui.Window;
import com.github.otbproject.otbproject.util.dev.DevHelper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.pircbotx.Configuration;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by justin on 02/01/2015.
 */
public class App {
    public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    static Listener listener = new IrcListener();
    public static CustomBot bot;
    public static final Logger logger = LogManager.getLogger();
    public static Thread botThread;
    public static Runnable botRunnable;

    public static void main(String[] args) {
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                Window gui = new Window();// I know this variable "gui" is never used, that is just how it works okay.
            }
            doMain(args);
        } catch (Throwable t) {
            try {
                System.err.println("A fatal problem has occurred.");
                t.printStackTrace();
                System.err.println("Attempting to log problem.");
                // TODO log throwable
                File file = new File("OTBProjectFatal.log");
                file.createNewFile();
                PrintStream ps = new PrintStream(file);
                t.printStackTrace(ps);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(-10);
            }
        }
    }


    public static void doMain(String[] args){
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
        FSCommandLoader.LoadBotCommands();
        FSCommandLoader.LoadBotAliases();

        // TODO remove before release
        DevHelper.run(args);

        // Load configs
        Account account;
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT_FILE)) {
            account = APIConfig.readAccount(cmd.getOptionValue(ArgParser.Opts.ACCOUNT_FILE));
        } else {
            account = APIConfig.readAccount();
        }
        GeneralConfig generalConfig = APIConfig.readGeneralConfig();
        BotConfig botConfig = APIConfig.readBotConfig();

        // Get account info
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT)) {
            account.setName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT));
        }
        if (cmd.hasOption(ArgParser.Opts.OAUTH)) {
            account.setOauth(cmd.getOptionValue(ArgParser.Opts.OAUTH));
        }
        APIConfig.writeAccount(account);

        HashSet<String> channels = new HashSet<>(botConfig.currentChannels);

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
            APIChannel.join(channelName);
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
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

    public static class BotRunnable implements Runnable {
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
