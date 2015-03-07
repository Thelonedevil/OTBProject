package com.github.otbproject.otbproject.cli.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.Api;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.Arrays;

/**
 * Created by justin on 18/02/2015.
 */
public class CmdParser {
    public static void SuperParse(String[] strings) throws InvalidArgumentException, InvalidCLICommandException {
        try {
            switch (strings[0].toLowerCase()) {
                case "stop":
                    if (App.bot.isConnected()) {
                        App.bot.shutdown();
                    }
                    System.exit(0);
                    break;
                case "restart":
                    if (App.bot.isConnected()) {
                        App.bot.shutdown();
                    }
                    while (App.botThread.isAlive()) {
                    }
                    try {
                        App.botThread = new App.BotThread();
                        App.botThread.start();
                    } catch (IllegalThreadStateException e) {
                        App.logger.catching(e);
                    }
                    break;
                case "channel":
                    if (strings.length > 2)
                        channelParse(Arrays.copyOfRange(strings, 2, strings.length - 1), strings[1]);
                    break;
                case "join":
                        if (strings.length > 1){
                            Api.joinChannel(strings[1]);
                        }
                    break;
                case "leave":
                    if (strings.length > 1){
                        Api.leaveChannel(strings[1]);
                    }
                    break;
                default:
                    throw new InvalidCLICommandException("That Command is invalid. "+strings[0].toLowerCase()+" does not exist as a CLI command");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidCLICommandException("No command was ran, what even?");
        }
    }

    private static void channelParse(String[] words, String channel) {
        try {
            switch (words[0].toLowerCase()) {
                case "command":
                    if (words.length > 1) {
                        try {
                            commandParse(Arrays.copyOfRange(words, 1, words.length - 1), channel);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                    break;
                case "alias":
                    if (words.length > 1)
                        try {
                            aliasParse(Arrays.copyOfRange(words, 1, words.length - 1), channel);
                        } catch (InvalidArgumentException e) {
                            App.logger.catching(e);
                        }
                    break;
                case "user":
                    if (words.length > 1)
                        try {
                            userParse(Arrays.copyOfRange(words, 1, words.length - 1), channel);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    break;
                default:
                    //TODO do thing saying bad
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            App.logger.catching(e);
        }
    }

    private static void commandParse(String[] strings, String channel) throws InvalidArgumentException {
        try {
            switch (strings[0].toLowerCase()) {
                case "add":
                    if (strings.length > 1)
                        commandAdder(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "edit":
                    if (strings.length > 1)
                        commandEditor(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "delete":
                    if (strings.length > 1)
                        commandDeleter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "set-ul":
                    if (strings.length > 1)
                        commandULSetter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                default:
                    throw new InvalidArgumentException(strings);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            App.logger.catching(e);
        }
    }

    private static void aliasParse(String[] strings, String channel) throws InvalidArgumentException {
        try {
            switch (strings[0].toLowerCase()) {
                case "add":
                    if (strings.length > 1)
                        aliasAdder(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "edit":
                    if (strings.length > 1)
                        aliasEditor(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "delete":
                    if (strings.length > 1)
                        aliasDeleter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "set-ul":
                    if (strings.length > 1)
                        aliasULSetter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                default:
                    throw new InvalidArgumentException(strings);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            App.logger.catching(e);
        }
    }

    private static void userParse(String[] strings, String channel) throws InvalidArgumentException {
        try {
            switch (strings[0].toLowerCase()) {
                case "add":
                    if (strings.length > 1)
                        userAdder(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "edit":
                    if (strings.length > 1)
                        userEditor(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                case "delete":
                    if (strings.length > 1)
                        userDeleter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                    break;
                default:
                    throw new InvalidArgumentException(strings);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            App.logger.catching(e);
        }
    }

    private static void userDeleter(String[] strings, String channel) {

    }

    private static void userEditor(String[] strings, String channel) {

    }

    private static void userAdder(String[] strings, String channel) {

    }

    private static void aliasULSetter(String[] strings, String channel) {

    }

    private static void aliasDeleter(String[] strings, String channel) {

    }

    private static void aliasEditor(String[] strings, String channel) {

    }

    private static void aliasAdder(String[] strings, String channel) {

    }

    private static void commandAdder(String[] strings, String channel) {

    }

    private static void commandEditor(String[] strings, String channel) {

    }

    private static void commandDeleter(String[] strings, String channel) {

    }

    private static void commandULSetter(String[] strings, String channel) {

    }

    public static void printHelp() {
        App.logger.info("TODO ACTUALLY PRINT A HELP MESSAGE");
    }
}
