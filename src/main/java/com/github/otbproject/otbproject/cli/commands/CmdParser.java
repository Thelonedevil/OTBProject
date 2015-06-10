package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.api.Channels;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.gui.GuiApplication;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.util.Util;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.preload.PreloadLoader;
import com.google.common.collect.ImmutableSet;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CmdParser {

    public static final String CLEAR = "clear";
    public static final String EXEC = "exec";
    public static final String JOINCHANNEL = "join";
    public static final String LEAVECHANNEL = "leave";
    public static final String RELOAD = "reload";
    public static final String RESTART = "restart";
    public static final String STOP = "stop";
    public static final String HELP = "help";
    private static final HashMap<String, CliCommand> map = new HashMap<>();
    private static final CliCommand.Builder commandBuilder = new CliCommand.Builder();
    private static final ArrayList<String> args = new ArrayList<>();
    private static String responseStr = "";
    private static String source = "";

    public static class ClearTargets {
        public static final String CLI = "cli";
        public static final String HISTORY = "history";
        public static final String INFO = "info";
        public static final String LOG = "log";
        public static final String WINDOWS = "windows";
        public static HashSet<String> targets = new HashSet<>();

        static {
            targets.add(CLI);
            targets.add(HISTORY);
            targets.add(INFO);
            targets.add(LOG);
            targets.add(WINDOWS);
        }
    }

    static {
        // Add CLI commands
        if (!GraphicsEnvironment.isHeadless()) {
            initClear();
        }
        initExec();
        initJoinChannel();
        initLeaveChannel();
        initReload();
        //initRestart();
        initStop();
        initHelp();
    }

    public static ImmutableSet<String> getCommands() {
        return ImmutableSet.copyOf(map.keySet());
    }

    public static void from(String source) {
        CmdParser.source = source;
    }

    // TODO make thread-safe
    public static String processLine(String aLine) {
        //use a second Scanner to parse the content of each line

        aLine = aLine.trim();

        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter(" ");
        try {
            if (scanner.hasNext()) {
                App.logger.debug("Processing input line: " + aLine);
                String name = scanner.next().toLowerCase();
                while (scanner.hasNext()) {
                    args.add(scanner.next());
                }
                if (map.containsKey(name)) {
                    map.get(name).run();
                } else {
                    printHelpNoCommand(name);
                }
                if (!responseStr.isEmpty()) {
                    App.logger.debug(responseStr);
                }
            } else {
                App.logger.warn("Empty or invalid line. Unable to process.");
            }
            return responseStr;
        } finally {
            scanner.close();
            responseStr = "";
            args.clear();
            source = "";
        }
    }

    static void printHelpNoCommand(String term) {
        responseStr = term + ": command not found";
    }

    private static void initClear() {
        commandBuilder.withShortHelp("clear TARGET")
                .withLongHelp("Clears the specified window(s) or the CLI command history\n"
                        + "Valid targets are: " + ClearTargets.targets.stream().sorted().collect(Collectors.joining(", ")))
                .withAction(() -> {
                    if (args.isEmpty()) {
                        responseStr = "Not enough args for '" + CLEAR + "'";
                        return;
                    }

                    switch (args.get(0)) {
                        case ClearTargets.CLI:
                            GuiApplication.clearCliOutput();
                            break;
                        case ClearTargets.LOG:
                            GuiApplication.clearLog();
                            break;
                        case ClearTargets.INFO:
                            GuiApplication.clearInfo();
                            break;
                        case ClearTargets.WINDOWS:
                            GuiApplication.clearCliOutput();
                            GuiApplication.clearLog();
                            GuiApplication.clearInfo();
                            break;
                        case ClearTargets.HISTORY:
                            GuiApplication.clearHistory();
                            break;
                        default:
                            responseStr = "Invalid target to clear: " + args.get(0) + "\nValid targets are: " + ClearTargets.targets.stream().sorted().collect(Collectors.joining(", "));
                    }
                });
        map.put(CLEAR, commandBuilder.create());
    }

    private static void initExec() {
        commandBuilder.withShortHelp("exec CHANNEL COMMAND")
                .withLongHelp("Runs the command denoted by COMMAND in the channel denoted by CHANNEL")
                .withAction(() -> {
                    if (args.size() < 2) {
                        responseStr = "Not enough args for '" + EXEC + "'";
                        return;
                    }
                    String channelName = args.get(0).toLowerCase();
                    if (!Channels.in(channelName)) {
                        responseStr = "Not in channel: " + channelName;
                        return;
                    }
                    UserLevel ul = UserLevel.INTERNAL;
                    String command = args.get(1);
                    for (int i = 2; i < args.size(); i++) {
                        command += " ";
                        command += args.get(i);
                    }
                    String destinationChannel = InternalMessageSender.DESTINATION_PREFIX + source;
                    PackagedMessage packagedMessage = new PackagedMessage(command, destinationChannel, channelName, destinationChannel, ul, MessagePriority.DEFAULT);
                    try {
                        Channels.get(channelName).receiveMessage(packagedMessage);
                        responseStr = "Command output above.";
                    } catch (NullPointerException npe) {
                        App.logger.catching(npe);
                    }
                });
        map.put(EXEC, commandBuilder.create());
    }

    private static void initJoinChannel() {
        commandBuilder.withShortHelp("join CHANNEL")
                .withLongHelp("Makes the bot join the channel denoted by CHANNEL")
                .withAction(() -> {
                    if (args.size() > 0) {
                        boolean success = Channels.join(args.get(0).toLowerCase(), true);
                        String string = success ? "Successfully joined" : "Failed to join";
                        responseStr = string + " channel: " + args.get(0).toLowerCase();
                    } else {
                        responseStr = "Not Enough Args for '" + JOINCHANNEL + "'";
                    }
                });
        map.put(JOINCHANNEL, commandBuilder.create());
    }

    private static void initLeaveChannel() {
        commandBuilder.withShortHelp("leave CHANNEL")
                .withLongHelp("Makes the bot leave the channel denoted by CHANNEL")
                .withAction(() -> {
                    if (args.size() > 0) {
                        boolean success = Channels.leave(args.get(0).toLowerCase());
                        String string = success ? "Successfully left" : "Failed to leave";
                        responseStr = string + " channel: " + args.get(0).toLowerCase();
                    } else {
                        responseStr = "Not Enough Args for '" + LEAVECHANNEL + "'";
                    }
                });
        map.put(LEAVECHANNEL, commandBuilder.create());
    }

    private static void initReload() {
        commandBuilder.withShortHelp("reload [CHANNEL]")
                .withLongHelp("Resets all commands from the json files. Either for CHANNEL, or if not specified, all channels.")
                .withAction(() -> {
                    if (args.size() > 0) {
                        PreloadLoader.loadDirectory(Base.CMD, Chan.SPECIFIC, args.get(0).toLowerCase(), LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectory(Base.CMD, Chan.ALL, args.get(0).toLowerCase(), LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectory(Base.ALIAS, Chan.SPECIFIC, args.get(0).toLowerCase(), LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectory(Base.ALIAS, Chan.ALL, args.get(0).toLowerCase(), LoadStrategy.FROM_LOADED);
                    } else {
                        PreloadLoader.loadDirectory(Base.CMD, Chan.ALL, null, LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectory(Base.ALIAS, Chan.ALL, null, LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectoryForEachChannel(Base.CMD, LoadStrategy.FROM_LOADED);
                        PreloadLoader.loadDirectoryForEachChannel(Base.ALIAS, LoadStrategy.FROM_LOADED);
                    }
                    responseStr += "Reload Complete";
                });
        map.put(RELOAD, commandBuilder.create());
    }

    private static void initRestart() {
        commandBuilder.withShortHelp("restart")
                .withLongHelp("Restarts the bot")
                .withAction(() -> {
                    if (Bot.getBot() != null && Bot.getBot().isConnected()) {
                        Bot.getBot().shutdown();
                    }
/*                    FSCommandLoader.LoadCommands();
                    FSCommandLoader.LoadAliases();*/
                    try {
                        Bot.setBotFuture(Util.getSingleThreadExecutor("Bot").submit(Bot.getBotRunnable()));
                    } catch (IllegalThreadStateException e) {
                        App.logger.catching(e);
                        responseStr = "Restart Failed";
                        return;
                    }
                    responseStr = "Restart Complete";
                });
        map.put(RESTART, commandBuilder.create());
    }

    private static void initStop() {
        commandBuilder.withShortHelp("stop")
                .withLongHelp("Stops the bot and exits.")
                .withAction(() -> {
                    App.logger.info("Stopping the process");
                    if (Bot.getBot() != null && Bot.getBot().isConnected()) {
                        Bot.getBot().shutdown();
                    }
                    App.logger.info("Process Stopped, Goodbye");
                    System.exit(0);
                });
        map.put(STOP, commandBuilder.create());
    }

    private static void initHelp() {
        commandBuilder.withShortHelp("help [CLI_COMMAND]")
                .withLongHelp("Prints the help message for the cli command denoted by CLI_COMMAND, or if not specified will list all the cli commands")
                .withAction(() -> {
                    if (args.isEmpty()) {
                        responseStr = map.keySet().stream().filter(key -> !key.equals(HELP)).map(key -> map.get(key).getShortHelp()).sorted().collect(Collectors.joining("\n"));
                        responseStr += "\n" + map.get(HELP).getShortHelp();
                    } else {
                        String arg = args.get(0);
                        if (map.keySet().contains(arg)) {
                            responseStr = map.get(arg).getFullHelp();
                        } else {
                            printHelpNoCommand(arg);
                        }
                    }
                });
        map.put(HELP, commandBuilder.create());
    }
}
