package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.bot.IBot;

import java.util.concurrent.Future;

public class Bot {
    private static IBot bot;
    private static Future<?> botFuture;
    private static Runnable botRunnable;

    public static IBot getBot() {
        return bot;
    }

    public static void setBot(IBot bot) {
        Bot.bot = bot;
    }

    public static Future<?> getBotFuture() {
        return botFuture;
    }

    public static void setBotFuture(Future<?> botFuture) {
        Bot.botFuture = botFuture;
    }

    public static Runnable getBotRunnable() {
        return botRunnable;
    }

    public static void setBotRunnable(Runnable botRunnable) {
        Bot.botRunnable = botRunnable;
    }
}
