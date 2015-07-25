package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.cli.ArgParser;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.config.ConfigManager;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.PathBuilder;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.fs.groups.Load;
import com.github.otbproject.otbproject.gui.GuiApplication;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.util.Unpacker;
import com.github.otbproject.otbproject.util.Util;
import com.github.otbproject.otbproject.util.compat.VersionCompatHelper;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.version.AppVersion;
import com.github.otbproject.otbproject.util.version.Version;
import com.github.otbproject.otbproject.util.version.Versions;
import com.github.otbproject.otbproject.web.WebInterface;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class App {
    public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    public static final Logger logger = LogManager.getLogger();
    public static final Version VERSION = AppVersion.current();
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
                File file = new File("OTBProjectFatal-" + dateFormat.format(date) + ".log");
                if (!file.createNewFile()) {
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


    private static void doMain(String[] args) {
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
        if (logFile.exists() && !logFile.delete()) {
            logger.error("Failed to delete old console log file");
        }

        // Log version
        logger.info("OTBProject version " + VERSION);

        // Read configs
        Bot.Control.loadConfigs(cmd);

        // Start GUI if applicable
        if (Bot.Graphics.present()) {
            Util.getSingleThreadExecutor().execute(() -> GuiApplication.start(args));
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
        Version version = Versions.readFromFile(versionFile).orElse(null); // TODO possibly not or else null?

        if (cmd.hasOption(ArgParser.Opts.UNPACK) || (VERSION.type != Version.Type.SNAPSHOT) && !VERSION.equals(version) && !cmd.hasOption(ArgParser.Opts.NO_UNPACK)) {
            if (!VERSION.equals(version)) {
                VersionCompatHelper.fixCompatIssues(version);
            }
            PathBuilder builder = new PathBuilder();
            Unpacker.unpack("preloads/json/commands/", builder.base(Base.CMD).channels(Chan.ALL).load(Load.TO).create());
            Unpacker.unpack("preloads/json/aliases/", builder.base(Base.ALIAS).channels(Chan.ALL).load(Load.TO).create());
            Unpacker.unpack("preloads/json/bot-channel/commands/", builder.base(Base.CMD).channels(Chan.BOT).load(Load.TO).create());
            Unpacker.unpack("preloads/groovy/scripts/", FSUtil.scriptDir());
            Bot.Control.loadPreloads(LoadStrategy.UPDATE);
        }
        Versions.writeToFile(versionFile, VERSION);

        // Perform various startup actions
        Bot.Control.firstStartup();

        // Start web interface
        if (Configs.getWebConfig().isEnabled()) {
            WebInterface.start();
        }

        if (Bot.Graphics.present()) {
            GuiApplication.setInputActive();
        }

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        System.out.println("Terminal input is now active.");
        while (scanner.hasNext()) {
            String in = scanner.next();
            if (!in.equals(""))
                System.out.println(CmdParser.processLine(in, InternalMessageSender.TERMINAL));
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

        if (cmd.hasOption(ArgParser.Opts.NO_GUI)) {
            Bot.Graphics.useGui(false);
        }

        return cmd;
    }

}
