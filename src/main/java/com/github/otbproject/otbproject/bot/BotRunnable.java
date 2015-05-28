package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class BotRunnable implements Runnable {
    @Override
    public void run() {
        try {
            Thread.currentThread().setName("Bot");
            App.logger.info("Bot Started");
            APIBot.getBot().startBot();
            App.logger.info("Bot Stopped");
        } catch (IOException | IrcException e) {
            App.logger.catching(e);
        }
    }
}
