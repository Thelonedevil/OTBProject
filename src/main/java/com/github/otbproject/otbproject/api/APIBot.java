package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.bot.IBot;

public class APIBot {
    private static IBot bot;
    private static Thread botThread;
    private static Runnable botRunnable;

    public static IBot getBot() {
        return bot;
    }

    public static void setBot(IBot bot) {
        APIBot.bot = bot;
    }

    public static Thread getBotThread() {
        return botThread;
    }

    public static void setBotThread(Thread botThread) {
        APIBot.botThread = botThread;
    }

    public static Runnable getBotRunnable() {
        return botRunnable;
    }

    public static void setBotRunnable(Runnable botRunnable) {
        APIBot.botRunnable = botRunnable;
    }
}
