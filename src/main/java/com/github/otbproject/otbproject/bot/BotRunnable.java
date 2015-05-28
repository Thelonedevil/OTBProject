package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

/**
* Created by Justin on 05/04/2015.
*/
public class BotRunnable implements Runnable {
    @Override
    public void run() {
        try {
            Thread.currentThread().setName("Bot");
            App.logger.info("Bot Started");
            APIBot.getBot().startBot();
            App.logger.info("Bot Stopped");
            for( String key : APIBot.getBot().getChannels().keySet()){
                APIChannel.get(key).getScheduler().getScheduledExecutorService().shutdown();
            }
        } catch (IOException | IrcException e) {
            App.logger.catching(e);
        }
    }
}
