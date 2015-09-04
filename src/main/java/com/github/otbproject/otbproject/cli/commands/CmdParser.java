package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.gui.GuiApplication;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.user.UserLevel;
import com.github.otbproject.otbproject.util.preload.LoadStrategy;
import com.github.otbproject.otbproject.util.preload.PreloadLoader;
import com.github.otbproject.otbproject.web.WebInterface;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdParser {

    public static final String CLEAR = "clear";
    public static final String EXEC = "exec";
    public static final String JOINCHANNEL = "join";
    public static final String LEAVECHANNEL = "leave";
    public static final String OPEN_WEB = "open-web";
    public static final String QUIT = "quit";
    public static final String RESET = "reset";
    public static final String RESTART = "restart";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String HELP = "help";
    private static final HashMap<String, CliCommand> map = new HashMap<>();
    private static final CliCommand.Builder commandBuilder = new CliCommand.Builder();
    private static List<String> args;
    private static String source = "";

    public static class ClearTargets {
        public static final String CLI = "cli";
        public static final String HISTORY = "history";
        public static final String INFO = "info";
        public static final String LOG = "log";
        public static final String WINDOWS = "windows";
        public static final Set<String> targets;

        static {
            Set<String> set = new HashSet<>();
            set.add(CLI);
            set.add(HISTORY);
            set.add(INFO);
            set.add(LOG);
            set.add(WINDOWS);
            targets = Collections.unmodifiableSet(set);
        }
    }

    static {
        // Add CLI commands
        if (Control.Graphics.present()) {
            initClear();
        }
        initExec();
        initJoinChannel();
        initLeaveChannel();
        initOpenWeb();
        initQuit();
        initReset();
        initRestart();
        initStart();
        initStop();
        initHelp();
    }

    public static Set<String> getCommands() {
        return Collections.unmodifiableSet(map.keySet());
    }

    public static synchronized String processLine(String line, String source) {
        CmdParser.source = source;
        String response = doLine(line);
        if (!response.isEmpty()) {
            App.logger.debug(response);
        }
        return response;
    }

    private static String doLine(String line) {
        args = Stream.of(line.trim().split(" ")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (!args.isEmpty()) {
            App.logger.debug("Processing input line: " + line);
            String cliCommand = args.get(0).toLowerCase();
            if (map.containsKey(cliCommand)) {
                return map.get(cliCommand).get();
            } else {
                return printHelpNoCommand(cliCommand);
            }
        } else {
            App.logger.warn("Empty or invalid line. Unable to process.");
        }
        return "";
    }

    static String printHelpNoCommand(String term) {
        return term + ": command not found";
    }

    private static void initClear() {
        commandBuilder.withShortHelp("clear TARGET")
                .withLongHelp("Clears the specified window(s) or the CLI command history\n"
                        + "Valid targets are: " + ClearTargets.targets.stream().sorted().collect(Collectors.joining(", ")))
                .withAction(() -> {
                    if (args.isEmpty()) {
                        return "Not enough args for '" + CLEAR + "'";
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
                            return "Invalid target to clear: " + args.get(0) + "\nValid targets are: " + ClearTargets.targets.stream().sorted().collect(Collectors.joining(", "));
                    }
                    return "";
                });
        map.put(CLEAR, commandBuilder.create());
    }

    private static void initExec() {
        commandBuilder.withShortHelp("exec CHANNEL COMMAND")
                .withLongHelp("Runs the command denoted by COMMAND in the channel denoted by CHANNEL")
                .withAction(() -> {
                    if (args.size() < 2) {
                        return "Not enough args for '" + EXEC + "'";
                    }
                    String channelName = args.get(0).toLowerCase();
                    if (!Channels.in(channelName)) {
                        return "Not in channel: " + channelName;
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
                        Channels.getOrThrow(channelName).receiveMessage(packagedMessage);
                        return "Command output above.";
                    } catch (ChannelNotFoundException e) {
                        App.logger.catching(e);
                        return "";
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
                        return string + " channel: " + args.get(0).toLowerCase();
                    } else {
                        return "Not enough args for '" + JOINCHANNEL + "'";
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
                        return string + " channel: " + args.get(0).toLowerCase();
                    } else {
                        return "Not Enough Args for '" + LEAVECHANNEL + "'";
                    }
                });
        map.put(LEAVECHANNEL, commandBuilder.create());
    }

    private static void initOpenWeb() {
        commandBuilder.withShortHelp("open-web")
                .withLongHelp("Opens the web interface in the default browser")
                .withAction(() -> {
                    WebInterface.openInBrowser();
                    return "";
                });
        map.put(OPEN_WEB, commandBuilder.create());
    }

    private static void initQuit() {
        commandBuilder.withShortHelp("quit")
                .withLongHelp("Stops the bot and exits")
                .withAction(() -> {
                    Control.shutdownAndExit();
                    return "";
                });
        map.put(QUIT, commandBuilder.create());
    }

    private static void initReset() {
        commandBuilder.withShortHelp("reset [CHANNEL]")
                .withLongHelp("Resets all commands and aliases to their defaults. Either for CHANNEL, or if not specified, all channels.")
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
                    return "Reset complete";
                });
        map.put(RESET, commandBuilder.create());
    }

    private static void initRestart() {
        commandBuilder.withShortHelp("restart")
                .withLongHelp("Restarts the bot")
                .withAction(() -> Control.restart() ? "Restarted bot" : "Failed to restart bot");
        map.put(RESTART, commandBuilder.create());
    }

    private static void initStart() {
        commandBuilder.withShortHelp("start")
                .withLongHelp("Starts the bot")
                .withAction(() -> {
                    try {
                        return (Control.startup() ? "Started bot" : "Did not start bot - bot already running");
                    } catch (Control.StartupException ignored) {
                        return "Failed to start bot";
                    }
                });
        map.put(START, commandBuilder.create());
    }

    private static void initStop() {
        commandBuilder.withShortHelp("stop")
                .withLongHelp("Stops the bot")
                .withAction(() -> (Control.shutdown(true) ? "Stopped bot" : "Bot not running"));
        map.put(STOP, commandBuilder.create());
    }

    private static void initHelp() {
        commandBuilder.withShortHelp("help [CLI_COMMAND]")
                .withLongHelp("Prints the help message for the cli command denoted by CLI_COMMAND, or if not specified will list all the cli commands")
                .withAction(() -> {
                    String responseStr;
                    if (args.isEmpty()) {
                        responseStr = map.keySet().stream().filter(key -> !key.equals(HELP)).map(key -> map.get(key).getShortHelp()).sorted().collect(Collectors.joining("\n"));
                        responseStr += "\n" + map.get(HELP).getShortHelp();
                        responseStr += "\n\nType \"help <command>\" to print the help message for a cli command.";
                    } else {
                        String arg = args.get(0);
                        if (map.keySet().contains(arg)) {
                            responseStr = map.get(arg).getFullHelp();
                        } else {
                            return printHelpNoCommand(arg);
                        }
                    }
                    return responseStr;
                });
        map.put(HELP, commandBuilder.create());
    }
}
