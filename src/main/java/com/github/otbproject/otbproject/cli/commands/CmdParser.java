package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.commands.loader.FSCommandLoader;
import com.github.otbproject.otbproject.commands.loader.LoadingSet;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CmdParser {

    public static final String STOP = "stop";
    public static final String RESTART = "restart";
    public static final String JOINCHANNEL = "join";
    public static final String LEAVECHANNEL = "leave";
    public static final String RELOAD = "reload";
    public static final String EXEC = "exec";
    final HashMap<String, Runnable> mapOfThings = new HashMap<>();
    final ArrayList<String> args = new ArrayList<>();
    private String responseStr = "";
    String name = "RETURN CHARACTER";//Honestly useless assignment, but something has to be here, why not this?

    public CmdParser() {
        //Java 8 magic
        mapOfThings.put(STOP, this::stop);
        mapOfThings.put(RESTART, this::restart);
        mapOfThings.put(JOINCHANNEL, this::joinChannel);
        mapOfThings.put(LEAVECHANNEL, this::leaveChannel);
        mapOfThings.put(RELOAD, this::reload);
        mapOfThings.put(EXEC, this::exec);
    }

    public String processLine(String aLine) {
        //use a second Scanner to parse the content of each line
        aLine = aLine.trim();
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter(" ");
        if (scanner.hasNext()) {
            App.logger.debug("Processing input line: " + aLine);
            name = scanner.next().toLowerCase();
            while (scanner.hasNext()) {
                args.add(scanner.next());
            }
            if (mapOfThings.containsKey(name)) {
                mapOfThings.get(name).run();
            } else {
                printHelpNoCommand();
            }
            if (!responseStr.isEmpty()) {
                App.logger.debug(responseStr);
            }
        } else {
            App.logger.warn("Empty or invalid line. Unable to process.");
        }
        return responseStr;
    }


    void stop() {
        App.logger.info("Stopping the process");
        if (APIBot.getBot() != null && APIBot.getBot().isConnected()) {
            APIBot.getBot().shutdown();
        }
        App.logger.info("Process Stopped, Goodbye");
        System.exit(0);
    }

    void restart() {
        if (APIBot.getBot() != null && APIBot.getBot().isConnected()) {
            APIBot.getBot().shutdown();
        }
        FSCommandLoader.LoadCommands();
        FSCommandLoader.LoadAliases();
        try {
            APIBot.setBotThread(new Thread(APIBot.getBotRunnable()));
            APIBot.getBotThread().start();
        } catch (IllegalThreadStateException e) {
            App.logger.catching(e);
        }
    }

    void joinChannel() {
        if (args.size() > 0) {
            APIChannel.join(args.get(0).toLowerCase(), true);
        }
    }

    void leaveChannel() {
        if (args.size() > 0) {
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

    void exec() {
        if (args.size() < 2) {
            responseStr = "Not enough args for 'exec'";
            return;
        }
        String channelName = args.get(0).toLowerCase();
        if (!APIChannel.in(channelName)) {
            responseStr = "Not in channel: " + channelName;
            return;
        }
        UserLevel ul = UserLevel.INTERNAL;
        String command = args.get(1);
        for (int i = 2; i < args.size(); i++) {
            command += " ";
            command += args.get(i);
        }
        PackagedMessage packagedMessage = new PackagedMessage(command, InternalMessageSender.DESTINATION_PREFIX + InternalMessageSender.CLI, channelName, InternalMessageSender.DESTINATION_PREFIX + InternalMessageSender.CLI, ul, MessagePriority.DEFAULT);
        try {
            APIChannel.get(channelName).receiveMessage(packagedMessage);
        } catch (NullPointerException npe) {
            App.logger.catching(npe);
        }
    }


    void printHelpNoCommand() {
        responseStr = "That command is invalid. \'" + name + "\' does not exist as a CLI command.";
    }

    void printHelp() {
        App.logger.info("Invalid arguments.");
    }

}
