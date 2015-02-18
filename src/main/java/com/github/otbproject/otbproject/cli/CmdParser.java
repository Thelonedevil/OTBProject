package com.github.otbproject.otbproject.cli;

import com.github.otbproject.otbproject.App;

import java.util.Arrays;

/**
 * Created by justin on 18/02/2015.
 */
public class CmdParser {
    public static void SuperParse(String[] words){
        switch(words[0]){
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
        }
    }

    private static void channelParse(String[] words,String channel){
        switch (words[0]){
            case "command":
                break;
            case "alias":
                break;
            case "user":
                break;
            default:
                
        }
    }
}
