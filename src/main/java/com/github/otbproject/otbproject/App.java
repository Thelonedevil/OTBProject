package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.bot.beam.BeamBot;
import com.github.otbproject.otbproject.bot.BotRunnable;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.fs.groups.*;
import com.github.otbproject.otbproject.gui.GuiApplication;
import com.github.otbproject.otbproject.bot.irc.IRCBot;
import com.github.otbproject.otbproject.bot.irc.InputParserImproved;
import com.github.otbproject.otbproject.util.LibsLoader;
import com.github.otbproject.otbproject.util.UnPacker;
import com.github.otbproject.otbproject.util.VersionClass;
import com.github.otbproject.otbproject.util.compat.VersionCompatHelper;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.preload.PreloadLoader;
import com.github.otbproject.otbproject.web.WarDownload;
import com.github.otbproject.otbproject.web.WebStart;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class App {
    public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    public static final Logger logger = LogManager.getLogger();
    public static final String VERSION = new VersionClass().getVersion();
    public static final ConfigManager configManager = new ConfigManager();

    public static void main(String[] args) {
        try {
            doMain(args);
        } catch (Throwable t) {
            try {
                System.err.println("A fatal problem has occurred.");
                t.printStackTrace();
                System.err.println("Attempting to log problem.");
                // log throwable
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
                Date date = new Date();
                File file = new File("OTBProjectFatal-"+dateFormat.format(date)+".log");
                if  (!file.createNewFile()) {
                    throw new IOException("Failed to create fatal log file for some reason.");
                }
                PrintStream ps = new PrintStream(file);
                t.printStackTrace(ps);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(-10);
            }
        }
    }



    private static void doMain(String[] args) throws NoSuchFieldException, IllegalAccessException {
        CommandLine cmd = initialArgParse(args);

        System.setProperty("OTBCONF", FSUtil.logsDir());
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger) LogManager.getLogger();
        LoggerContext context
                = coreLogger.getContext();
        org.apache.logging.log4j.core.config.Configuration config
                = context.getConfiguration();

        if (cmd.hasOption(ArgParser.Opts.DEBUG)) {
            coreLogger.removeAppender(config.getAppender("Console-info"));
            coreLogger.removeAppender(config.getAppender("Routing-console-info"));
        } else {
            coreLogger.removeAppender(config.getAppender("Console-debug"));
            coreLogger.removeAppender(config.getAppender("Routing-console-debug"));
        }
        File logFile = new File(FSUtil.logsDir() + File.separator + "console.log");
        logFile.delete();

        // Log version
        logger.info("OTBProject version " + VERSION);
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                App.logger.catching(e);
            }
            new Thread(() -> {
                GuiApplication.start(args);
            }).start();
        }

        // Ensure directory tree is setup
        try {
            Setup.setup();
        } catch (IOException e) {
            logger.error("Unable to setup main directory tree at:\t" + FSUtil.getBaseDir());
            logger.catching(e);
            System.exit(1);
        }

        File versionFile = new File(FSUtil.configDir() + File.separator + "VERSION");
        try {
            if (!versionFile.exists() && !versionFile.createNewFile()) {
                throw new IOException("Failed to create version file");
            }
        } catch (IOException e) {
            logger.catching(e);
        }
        String version = "";
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(versionFile));
            version = fileReader.readLine();
        } catch (IOException e) {
            logger.catching(e);
        }
        if (cmd.hasOption(ArgParser.Opts.UNPACK) || (!VERSION.contains("SNAPSHOT") && !VERSION.equalsIgnoreCase(version) && !cmd.hasOption(ArgParser.Opts.NO_UNPACK))) {
            if (!VERSION.equalsIgnoreCase(version)) {
                VersionCompatHelper.fixCompatIssues(version);
            }
            // TODO unpack based on a list or something
            UnPacker.unPack("preloads/json/commands/", FSUtil.builder.base(Base.CMD).channels(Chan.ALL).load(Load.TO).create());
            UnPacker.unPack("preloads/json/aliases/", FSUtil.builder.base(Base.ALIAS).channels(Chan.ALL).load(Load.TO).create());
            UnPacker.unPack("preloads/json/bot-channel/commands/", FSUtil.builder.base(Base.CMD).channels(Chan.BOT).load(Load.TO).create());
            UnPacker.unPack("preloads/groovy/scripts/", FSUtil.scriptDir());
            loadPreloads(LoadStrategy.UPDATE);
        }
        try {
            PrintStream ps = new PrintStream(versionFile);
            ps.println(VERSION);
        } catch (IOException e) {
            logger.catching(e);
        }

        // TODO remove in later release
        // Must be done after version check for version 1.1 so the compatibility fix will work
        Setup.createAccountFiles();

        // Perform various startup actions
        startup(cmd);

        // Connect to service
        switch (APIConfig.getGeneralConfig().getServiceName()){
            case TWITCH:
                APIBot.setBot(new IRCBot());
                Class c = APIBot.getBot().getClass().getSuperclass();
                Field input = c.getDeclaredField("inputParser");
                input.setAccessible(true);
                input.set(APIBot.getBot(), new InputParserImproved((IRCBot) APIBot.getBot()));
                break;
            case BEAM:
                APIBot.setBot(new BeamBot());
                break;
        }
        APIBot.setBotRunnable(new BotRunnable());
        APIBot.setBotThread(new Thread(APIBot.getBotRunnable()));
        APIBot.getBotThread().start();
        try{
            if(new File(WebStart.WAR_PATH).exists()){
                WebStart.main(args);
            }else if(!VERSION.contains("-SNAPSHOT")){
                Thread thread = new Thread(new WarDownload());
                thread.start();
                thread.join();
                WebStart.main(args);
            }else{
                App.logger.error("You are running a dev build of OTBProject, please also grab the latest build of the web interface and place in \""+FSUtil.webDir()+File.separator+"\" as \"WebInterface-"+new VersionClass().getVersion()+".war\". Releases will automatically download this for you");
            }

        }catch (Exception e){
            logger.catching(e);
        }
        if (!GraphicsEnvironment.isHeadless()) {
            GuiApplication.setInputActive();
        }

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            String in = scanner.next();
            if (!in.equals(""))
                CmdParser.processLine(in);
        }
        scanner.close();
    }

    private static CommandLine initialArgParse(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = ArgParser.parse(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            ArgParser.printHelp();
            System.exit(1);
        }

        if (cmd.hasOption(ArgParser.Opts.HELP)) {
            ArgParser.printHelp();
            System.exit(0);
        }

        if (cmd.hasOption(ArgParser.Opts.VERSION)) {
            System.out.println("OTBProject version " + VERSION);
            System.exit(0);
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
                System.exit(2);
            }
        }

        return cmd;
    }

    private static void loadPreloads(LoadStrategy strategy) {
        PreloadLoader.loadDirectory(Base.CMD, Chan.ALL, null, strategy);
        PreloadLoader.loadDirectory(Base.ALIAS, Chan.ALL, null, strategy);
        PreloadLoader.loadDirectory(Base.CMD, Chan.BOT, null, strategy);
        PreloadLoader.loadDirectory(Base.ALIAS, Chan.BOT, null, strategy);

        PreloadLoader.loadDirectoryForEachChannel(Base.CMD, strategy);
        PreloadLoader.loadDirectoryForEachChannel(Base.ALIAS, strategy);
    }

    public static void startup(CommandLine cmd) {
        // Load commands and aliases
        loadPreloads(LoadStrategy.OVERWRITE);

        loadConfigs(cmd);

        LibsLoader.load();
    }

    public static void loadConfigs(CommandLine cmd) {
        // General config
        GeneralConfig generalConfig = APIConfig.readGeneralConfig(); // Must be read first for service info
        configManager.setGeneralConfig(generalConfig);
        if (cmd.hasOption(ArgParser.Opts.SERVICE)) {
            String serviceName = cmd.getOptionValue(ArgParser.Opts.SERVICE).toUpperCase();
            if (serviceName.equals(ServiceName.TWITCH.toString())) {
                APIConfig.getGeneralConfig().setServiceName(ServiceName.TWITCH);
            } else if (serviceName.equals(ServiceName.BEAM.toString())) {
                APIConfig.getGeneralConfig().setServiceName(ServiceName.BEAM);
            } else {
                logger.error("Invalid service name: " + serviceName);
                ArgParser.printHelp();
                System.exit(1);
            }
            APIConfig.writeGeneralConfig();
        }

        // Account config
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT_FILE)) {
            APIConfig.setAccountFileName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT_FILE));
        }
        Account account = APIConfig.readAccount();
        if (cmd.hasOption(ArgParser.Opts.ACCOUNT)) {
            account.setName(cmd.getOptionValue(ArgParser.Opts.ACCOUNT));
        }
        if (cmd.hasOption(ArgParser.Opts.PASSKEY)) {
            account.setPasskey(cmd.getOptionValue(ArgParser.Opts.PASSKEY));
        }
        configManager.setAccount(account);
        APIConfig.writeAccount();

        // Bot config
        BotConfig botConfig = APIConfig.readBotConfig();
        configManager.setBotConfig(botConfig);
    }
}
