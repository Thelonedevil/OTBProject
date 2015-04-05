package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.commands.loader.FSCommandLoader;
import com.github.otbproject.otbproject.config.Account;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.GeneralConfig;
import com.github.otbproject.otbproject.config.ServiceName;
import com.github.otbproject.otbproject.eventlistener.IrcListener;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.gui.Window;
import com.github.otbproject.otbproject.util.InputParserImproved;
import com.github.otbproject.otbproject.util.UnPacker;
import com.github.otbproject.otbproject.util.VersionClass;
import com.github.otbproject.otbproject.util.compat.VersionCompatHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.pircbotx.Configuration;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by justin on 02/01/2015.
 */
public class App {
    public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    public static final Logger logger = LogManager.getLogger();
    public static final String VERSION = new VersionClass().getVersion();
    public static CustomBot bot;
    public static Thread botThread;
    public static Runnable botRunnable;
    static Listener listener = new IrcListener();

    public static void main(String[] args) {
        try {
            doMain(args);
        } catch (Throwable t) {
            try {
                System.err.println("A fatal problem has occurred.");
                t.printStackTrace();
                System.err.println("Attempting to log problem.");
                // log throwable
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



    public static void doMain(String[] args) throws NoSuchFieldException, IllegalAccessException {
        CommandLine cmd;
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
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger) LogManager.getLogger();
        LoggerContext context
                = coreLogger.getContext();
        org.apache.logging.log4j.core.config.Configuration config
                = context.getConfiguration();

        if (cmd.hasOption(ArgParser.Opts.DEBUG)) {
            coreLogger.removeAppender(config.getAppender("Console-info"));
        } else {
            coreLogger.removeAppender(config.getAppender("Console-debug"));
        }

        // Ensure directory tree is setup
        try {
            Setup.setup();
        } catch (IOException e) {
            App.logger.error("Unable to setup main directory tree at:\t" + FSUtil.getBaseDir());
            App.logger.catching(e);
            System.exit(1);
        }

        File versionFile = new File(FSUtil.configDir() + File.separator + "VERSION");
        try {
            versionFile.createNewFile();
        } catch (IOException e) {
            App.logger.catching(e);
        }
        String version = "";
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(versionFile));
            version = fileReader.readLine();
        } catch (IOException e) {
            App.logger.catching(e);
        }
        if (cmd.hasOption(ArgParser.Opts.UNPACK) || (!VERSION.contains("SNAPSHOT") && !VERSION.equalsIgnoreCase(version))) {
            if (!VERSION.equalsIgnoreCase(version)) {
                VersionCompatHelper.fixCompatIssues(VERSION, version);
            }
            UnPacker.unPack("preloads/json/commands/", FSUtil.commandsDir() + File.separator + "all-channels" + File.separator + "to-load");
            UnPacker.unPack("preloads/json/aliases/", FSUtil.aliasesDir() + File.separator + "all-channels" + File.separator + "to-load");
            UnPacker.unPack("preloads/json/bot-channel/commands/", FSUtil.commandsDir() + File.separator + "bot-channel" + File.separator + "to-load");
            UnPacker.unPack("preloads/groovy/scripts/", FSUtil.scriptDir());
        }
        try {
            PrintStream ps = new PrintStream(versionFile);
            ps.println(VERSION);
        } catch (IOException e) {
            App.logger.catching(e);
        }


        FSCommandLoader.LoadCommands();
        FSCommandLoader.LoadAliases();
        FSCommandLoader.LoadBotCommands();
        FSCommandLoader.LoadBotAliases();

        // Load configs
        GeneralConfig generalConfig = APIConfig.readGeneralConfig(); // Must be read first for service info
        if (cmd.hasOption(ArgParser.Opts.SERVICE)) {
            String serviceName = cmd.getOptionValue(ArgParser.Opts.SERVICE).toUpperCase();
            if (serviceName.equals(ServiceName.TWITCH.toString())) {
                APIConfig.getGeneralConfig().setServiceName(ServiceName.TWITCH);
            } else if (serviceName.equals(ServiceName.BEAM.toString())) {
                APIConfig.getGeneralConfig().setServiceName(ServiceName.BEAM);
            } else {
                App.logger.error("Invalid service name: " + serviceName);
                ArgParser.printHelp();
                System.exit(1);
            }
            APIConfig.writeGeneralConfig();
        }

        Account account;
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT_FILE)) {
            account = APIConfig.readAccount(cmd.getOptionValue(ArgParser.Opts.ACCOUNT_FILE));
        } else {
            account = APIConfig.readAccount();
        }

        BotConfig botConfig = APIConfig.readBotConfig();

        // Get account info
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT)) {
            account.setName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT));
        }
        if (cmd.hasOption(ArgParser.Opts.PASSKEY)) {
            account.setPassKey(cmd.getOptionValue(ArgParser.Opts.PASSKEY));
        }
        APIConfig.writeAccount(account);

        //TODO get botname and oauth from config asell as possible server address and port
        Configuration.Builder configurationBuilder = new Configuration.Builder().setName(account.getName()).setAutoNickChange(false).setCapEnabled(false).addListener(listener).setServerHostname("irc.twitch.tv")
                .setServerPort(6667).setServerPassword(account.getPassKey()).setEncoding(Charset.forName("UTF-8"));
        Configuration configuration = configurationBuilder.buildConfiguration();

        logger.info("Bot configuration built");
        bot = new CustomBot(configuration);
        Class c = bot.getClass().getSuperclass();
        Field input = c.getDeclaredField("inputParser");
        input.setAccessible(true);
        input.set(bot, new InputParserImproved(bot));
        // Store configs
        bot.configManager.setAccount(account);
        bot.configManager.setGeneralConfig(generalConfig);
        bot.configManager.setBotConfig(botConfig);
        botRunnable = new BotRunnable();
        botThread = new Thread(botRunnable);
        botThread.start();

        if (!GraphicsEnvironment.isHeadless()) {
            Window gui = new Window();// I know this variable "gui" is never used, that is just how it works okay.
        }
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            String in = scanner.next();
            if (!in.equals(""))
                new CmdParser().processLine(in);
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
