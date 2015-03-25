package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.*;
import com.github.otbproject.otbproject.users.User;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.users.Users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Justin on 25/03/2015.
 */
public class CmdParser {

    HashMap<String, Runnable> mapOfThings = new HashMap<>();
    public static final String STOP = "stop";
    public static final String RESTART = "restart";
    public static final String JOINCHANNEL = "join";
    public static final String LEAVECHANNEL = "leave";
    public static final String RELOAD = "reload";
    public static final String COMMAND = "command";
    public static final String ALIAS = "alias";
    public static final String USER = "user";

    ArrayList<String> args = new ArrayList<>();
    String name = "RETURN CHARACTER";//Honestly useless assignment, but something has to be here, why not this?

    public CmdParser() {
        //Java 8 magic
        mapOfThings.put(STOP, this::stop);
        mapOfThings.put(RESTART, this::restart);
        mapOfThings.put(JOINCHANNEL, this::joinChannel);
        mapOfThings.put(LEAVECHANNEL, this::leaveChannel);
        mapOfThings.put(RELOAD, this::reload);
        mapOfThings.put(COMMAND, this::command);
        mapOfThings.put(ALIAS, this::alias);
        mapOfThings.put(USER, this::user);
    }

    public void processLine(String aLine) {
        //use a second Scanner to parse the content of each line
        aLine = aLine.trim();
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter(" ");
        if (scanner.hasNext()) {
            App.logger.info(aLine);
            name = scanner.next().toLowerCase();
            while (scanner.hasNext()) {
                args.add(scanner.next());
            }
            if (mapOfThings.containsKey(name)) {
                mapOfThings.get(name).run();
            } else {
                printHelpNoCommand();
            }
        } else {
            App.logger.info("Empty or invalid line. Unable to process.");
        }
    }


    void stop() {
        App.logger.info("Stopping the process");
        if (App.bot.isConnected()) {
            App.bot.shutdown();
        }
        App.logger.info("Process Stopped, Goodbye");
        System.exit(0);
    }

    void restart() {
        if (App.bot.isConnected()) {
            App.bot.shutdown();
        }
        FSCommandLoader.LoadCommands();
        FSCommandLoader.LoadAliases();
        try {
            App.botThread = new Thread(App.botRunnable);
            App.botThread.start();
        } catch (IllegalThreadStateException e) {
            App.logger.catching(e);
        }
    }

    void joinChannel() {
        if (args.size() > 0 && !APIChannel.in(args.get(0).toLowerCase())) {
            APIChannel.join(args.get(0).toLowerCase(), true);
        }
    }

    void leaveChannel() {
        if (args.size() > 0 && !APIChannel.in(args.get(0).toLowerCase())) {
            APIChannel.leave(args.get(0).toLowerCase());
        }
    }

    void reload() {
        if (args.size() > 0) {
            try {
                FSCommandLoader.LoadLoadedCommands(args.get(0).toLowerCase(), LoadingSet.BOTH);
            } catch (IOException e) {
                App.logger.catching(e);
            }
            try {
                FSCommandLoader.LoadLoadedAliases(args.get(0).toLowerCase(), LoadingSet.BOTH);
            } catch (IOException e) {
                App.logger.catching(e);
            }
        } else {
            FSCommandLoader.LoadLoadedCommands(LoadingSet.BOTH);
            FSCommandLoader.LoadLoadedAliases(LoadingSet.BOTH);
        }
    }

    void user() {
        if (args.size() > 3) {
            String channelname = args.get(0).toLowerCase();
            String username = args.get(1).toLowerCase();
            String userLevel = args.get(2);
            User user = new User();
            user.setNick(username);
            UserLevel ul = UserLevel.DEFAULT;
            switch (userLevel) {
                case "subscriber":
                case "sub":
                    printHelpUnSupportedUL();
                    break;
                case "regular":
                case "reg":
                    ul = UserLevel.REGULAR;
                    break;
                case "moderator":
                case "mod":
                    printHelpUnSupportedUL();
                    break;
                case "super-moderator":
                case "super_moderator":
                case "smod":
                case "sm":
                    ul = UserLevel.SUPER_MODERATOR;
                    break;
                case "broadcaster":
                case "bc":
                    printHelpUnSupportedUL();
                    break;
                case "default":
                case "def":
                case "none":
                case "any":
                case "all":
                    ul = UserLevel.DEFAULT;
                    break;
                case "ignored":
                case "ig":
                    ul = UserLevel.IGNORED;
                    break;
                case "reset":
                case "twitch":
                    Users.remove(APIChannel.get(channelname).getDatabaseWrapper(), username);
                    break;
                default:
                    printHelpUnSupportedUL();
            }
            user.setUserLevel(ul);
            Users.addUserFromObj(APIChannel.get(channelname).getDatabaseWrapper(), user);
        }
    }

    void alias() {
        if (args.size() > 1) {
            String channelname = args.get(1).toLowerCase();
            switch (args.get(0).toLowerCase()) {
                case "add":
                    if (args.size() > 3) {
                        String aliasName = args.get(2);
                        String commandName = args.get(3);
                        LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
                        alias.setName(aliasName);
                        alias.setCommand(commandName);
                        Alias.addAliasFromLoadedAlias(APIChannel.get(channelname).getDatabaseWrapper(), alias);
                    }
                    break;
                case "edit":
                    if (args.size() > 3) {
                        String aliasName = args.get(2).toLowerCase();
                        String commandName = args.get(3).toLowerCase();
                        LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
                        if (Alias.exists(APIChannel.get(channelname).getDatabaseWrapper(), aliasName)) {
                            alias = Alias.get(APIChannel.get(channelname).getDatabaseWrapper(), aliasName);
                        }
                        alias.setName(aliasName);
                        alias.setCommand(commandName);
                        Alias.addAliasFromLoadedAlias(APIChannel.get(channelname).getDatabaseWrapper(), alias);

                        break;
                    }
                case "delete":
                    if (args.size() > 2) {
                        String aliasName = args.get(2);
                        Alias.remove(APIChannel.get(channelname).getDatabaseWrapper(), aliasName);
                    }
                    break;
                case "enable":
                    if (args.size() > 2) {
                        String aliasName = args.get(2);
                        LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
                        if (Alias.exists(APIChannel.get(channelname).getDatabaseWrapper(), aliasName)) {
                            alias = Alias.get(APIChannel.get(channelname).getDatabaseWrapper(), aliasName);
                        }
                        alias.setEnabled(true);
                        Alias.addAliasFromLoadedAlias(APIChannel.get(channelname).getDatabaseWrapper(), alias);
                    }
                    break;
                case "disable":
                    if (args.size() > 2) {
                        String aliasName = args.get(2);
                        LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
                        if (Alias.exists(APIChannel.get(channelname).getDatabaseWrapper(), aliasName)) {
                            alias = Alias.get(APIChannel.get(channelname).getDatabaseWrapper(), aliasName);
                        }
                        alias.setEnabled(false);
                        Alias.addAliasFromLoadedAlias(APIChannel.get(channelname).getDatabaseWrapper(), alias);
                    }
                    break;
                default:
                    printHelpWrongAliasArgs();

            }
        }
    }

    void command() {
        if (args.size() > 1) {
            String channelname = args.get(1).toLowerCase();
            switch (args.get(0).toLowerCase()) {
                case "add":
                    if (args.size() > 6) {
                        String ul = args.get(2).split("=")[1];
                        String ma = args.get(3).split("=")[1];
                        String commandName = args.get(4);
                        String response = args.get(5);
                        for (int i = 6; i < args.size(); i++) {
                            response += " ";
                            response += args.get(i);
                        }
                        LoadedCommand command = DefaultCommandGenerator.createDefaultCommand();
                        command.setName(commandName);
                        command.setResponse(response);
                        UserLevel execUL = UserLevel.valueOf(ul);
                        if (execUL != UserLevel.IGNORED) {
                            command.setExecUserLevel(execUL);
                        }
                        int minArgs = Integer.parseInt(ma);
                        if (minArgs != -1) {
                            command.setMinArgs(minArgs);
                        }
                        Command.addCommandFromLoadedCommand(APIChannel.get(channelname).getDatabaseWrapper(), command);
                    }
                    break;
                case "edit":
                    if (args.size() > 6) {
                        String ul = args.get(2).split("=")[1];
                        String ma = args.get(3).split("=")[1];
                        String commandName = args.get(4);
                        String response = args.get(5);
                        for (int i = 6; i < args.size(); i++) {
                            response += " ";
                            response += args.get(i);
                        }
                        LoadedCommand command = DefaultCommandGenerator.createDefaultCommand();
                        if (Command.exists(APIChannel.get(channelname).getDatabaseWrapper(), commandName)) {
                            command = Command.get(APIChannel.get(channelname).getDatabaseWrapper(), commandName);
                        }
                        command.setResponse(response);
                        UserLevel execUL = UserLevel.valueOf(ul);
                        if (execUL != UserLevel.IGNORED) {
                            command.setExecUserLevel(execUL);
                        }
                        int minArgs = Integer.parseInt(ma);
                        if (minArgs != -1) {
                            command.setMinArgs(minArgs);
                        }
                        Command.addCommandFromLoadedCommand(APIChannel.get(channelname).getDatabaseWrapper(), command);
                    }
                    break;
                case "delete":
                    if (args.size() > 2) {
                        String commandName = args.get(2).toLowerCase();
                        Command.remove(APIChannel.get(channelname).getDatabaseWrapper(), commandName);
                    }
                    break;
                case "enable":
                    if (args.size() > 2) {
                        String commandName = args.get(2);
                        LoadedCommand command = DefaultCommandGenerator.createDefaultCommand();
                        if (Command.exists(APIChannel.get(channelname).getDatabaseWrapper(), commandName)) {
                            command = Command.get(APIChannel.get(channelname).getDatabaseWrapper(), commandName);
                        }
                        command.setEnabled(true);
                        Command.addCommandFromLoadedCommand(APIChannel.get(channelname).getDatabaseWrapper(), command);
                    }
                    break;
                case "disable":
                    if (args.size() > 2) {
                        String commandName = args.get(2);
                        LoadedCommand command = DefaultCommandGenerator.createDefaultCommand();
                        if (Command.exists(APIChannel.get(channelname).getDatabaseWrapper(), commandName)) {
                            command = Command.get(APIChannel.get(channelname).getDatabaseWrapper(), commandName);
                        }
                        command.setEnabled(false);
                        Command.addCommandFromLoadedCommand(APIChannel.get(channelname).getDatabaseWrapper(), command);
                    }
                    break;
                default:
                    printHelpWrongCommandArgs();
            }
        }
    }

    void printHelpUnSupportedUL() {
        App.logger.info("Valid User Levels for \"User\" are; default | def | none | any | all, subscriber | sub," +
                "regular | reg, moderator | mod, super-moderator | super_moderator | smod | sm, broadcaster | bc. Assuming Default.");
    }

    void printHelpWrongAliasArgs() {
        App.logger.info("Valid sub-commands for \"Alias\" are; Add, Edit, Delete, Enable, Disable.");
    }

    void printHelpWrongCommandArgs() {
        App.logger.info("Valid sub-commands for \"Command\" are; Add, Edit, Delete, Enable, Disable.");
    }

    void printHelpNoCommand() {
        App.logger.info("That command is invalid. \'" + name + "\' does not exist as a CLI command.");
    }

    void printHelp() {
        App.logger.info("Invalid arguments.");
    }

}
