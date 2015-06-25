package com.github.otbproject.otbproject.config;

public class BotConfigHelper {
    public static boolean addToWhitelist(BotConfig botConfig, String channel) {
        if (!botConfig.whitelist.contains(channel)) {
            botConfig.whitelist.add(channel);
            return true;
        }
        return false;
    }

    public static boolean removeFromWhitelist(BotConfig botConfig, String channel) {
        return botConfig.whitelist.remove(channel);
    }

    public static void clearWhitelist(BotConfig botConfig) {
        botConfig.whitelist.clear();
    }

    public static boolean isWhitelisted(BotConfig botConfig, String channel) {
        return botConfig.whitelist.contains(channel);
    }

    public static boolean addToBlacklist(BotConfig botConfig, String channel) {
        if (!botConfig.blacklist.contains(channel)) {
            botConfig.blacklist.add(channel);
            return true;
        }
        return false;
    }

    public static boolean removeFromBlacklist(BotConfig botConfig, String channel) {
        return botConfig.blacklist.remove(channel);
    }

    public static void clearBlacklist(BotConfig botConfig) {
        botConfig.blacklist.clear();
    }

    public static boolean isBlacklisted(BotConfig botConfig, String channel) {
        return botConfig.blacklist.contains(channel);
    }

    public static boolean addToCurrentChannels(BotConfig botConfig, String channel) {
        if (!botConfig.currentChannels.contains(channel)) {
            botConfig.currentChannels.add(channel);
            return true;
        }
        return false;
    }

    public static boolean removeFromCurrentChannels(BotConfig botConfig, String channel) {
        return botConfig.currentChannels.remove(channel);
    }

    public static void clearCurrentChannels(BotConfig botConfig) {
        botConfig.currentChannels.clear();
    }

    public static boolean isInChannel(BotConfig botConfig, String channel) {
        return botConfig.currentChannels.contains(channel);
    }
}
