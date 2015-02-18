package com.github.otbproject.otbproject.cli;

import com.github.otbproject.otbproject.App;

import java.util.Arrays;

/**
 * Created by justin on 18/02/2015.
 */
public class CmdParser {
    public static void SuperParse(String[] words){
        switch(words[0].toLowerCase()){
            case "stop":
                if(App.bot.isConnected()) {
                    App.bot.shutdown();
                }
                System.exit(0);
                break;
            case "restart":
                App.bot.shutdown();
                while(App.botThread.isAlive()){}
                try {
                    App.botThread = new App.BotThread();
                    App.botThread.start();
                }catch (IllegalThreadStateException e){
                    App.logger.catching(e);
                }
                break;
            case "channel":
                if(words.length > 2)
                channelParse(Arrays.copyOfRange(words,2,words.length-1), words[1]);
                break;
            default:
                //TODO do thing saying bad
        }
    }

    private static void channelParse(String[] words,String channel){
        switch (words[0].toLowerCase()){
            case "command":
                    if(words.length > 1)
                    commandParse(Arrays.copyOfRange(words,1,words.length-1),channel);
                break;
            case "alias":
                break;
            case "user":
                break;
            default:
                //TODO do thing saying bad
        }
    }

    private static void commandParse(String[] strings, String channel) {
        switch (strings[0].toLowerCase()){
            case "add":
                commandAdder(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                break;
            case "edit":
                commandEditor(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                break;
            case "delete":
                commandDeleter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                break;
            case "set-ul":
                commandULSetter(Arrays.copyOfRange(strings, 1, strings.length - 1), channel);
                break;
            default:
        }
    }

    private static void commandAdder(String[] strings, String channel) {

    }
    private static void commandEditor(String[] strings, String channel) {

    }
    private static void commandDeleter(String[] strings, String channel) {

    }
    private static void commandULSetter(String[] strings, String channel) {

    }
}
