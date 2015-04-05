package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;

public class APIConfig {
    public static final String GENERAL_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG;
    public static final String BOT_CONFIG_PATH = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.ConfigFileNames.BOT_CONFIG;

    // Reading
    public static Account readAccount(String filename) {
        Account account = ConfigValidator.validateAccount(JsonHandler.readValue(getAccountPath(filename), Account.class));
        writeAccount(account, filename);
        return account;
    }

    public static Account readAccount() {
        return readAccount(getAccountFileName());
    }

    public static GeneralConfig readGeneralConfig() {
        GeneralConfig config = ConfigValidator.validateGeneralConfig(JsonHandler.readValue(GENERAL_CONFIG_PATH, GeneralConfig.class));
        writeGeneralConfig(config);
        return config;
    }

    public static BotConfig readBotConfig() {
        BotConfig config = ConfigValidator.validateBotConfig(JsonHandler.readValue(BOT_CONFIG_PATH, BotConfig.class));
        writeBotConfig(config);
        return config;
    }

    public static ChannelConfig readChannelConfig(String channel) {
        ChannelConfig config = ConfigValidator.validateChannelConfig(JsonHandler.readValue(getChannelPath(channel), ChannelConfig.class));
        writeChannelConfig(config, channel);
        return config;
    }

    // Writing
    public static void writeAccount(Account account, String filename) {
        JsonHandler.writeValue(getAccountPath(filename), account);
    }

    public static void writeAccount(Account account) {
        writeAccount(account, getAccountFileName());
    }

    public static void writeAccount(String filename) {
        writeAccount(getAccount(), filename);
    }

    public static void writeAccount() {
        writeAccount(getAccount());
    }

    public static void writeGeneralConfig(GeneralConfig config) {
        JsonHandler.writeValue(GENERAL_CONFIG_PATH, config);
    }

    public static void writeGeneralConfig() {
        writeGeneralConfig(getGeneralConfig());
    }

    public static void writeBotConfig(BotConfig config) {
        JsonHandler.writeValue(BOT_CONFIG_PATH, config);
    }

    public static void writeBotConfig() {
        writeBotConfig(getBotConfig());
    }

    public static void writeChannelConfig(ChannelConfig config, String channel) {
        JsonHandler.writeValue(getChannelPath(channel), config);
    }

    public static void writeChannelConfig(String channel) {
        writeChannelConfig(getChannelConfig(channel), channel);
    }

    public static Account getAccount() {
        return App.configManager.getAccount();
    }

    public static GeneralConfig getGeneralConfig() {
        return App.configManager.getGeneralConfig();
    }

    public static BotConfig getBotConfig() {
        return App.configManager.getBotConfig();
    }

    public static ChannelConfig getChannelConfig(String channel) {
        if (!App.bot.channels.containsKey(channel)) {
            return null;
        }
        return APIChannel.get(channel).getConfig();
    }

    public static String getAccountFileName() {
        ServiceName serviceName = getGeneralConfig().getServiceName();
        if (serviceName == ServiceName.BEAM) {
            return FSUtil.ConfigFileNames.ACCOUNT_BEAM;
        } else { // Defaults to Twitch
            return FSUtil.ConfigFileNames.ACCOUNT_TWITCH;
        }
    }

    private static String getAccountPath(String filename) {
        return FSUtil.configDir() + File.separator + filename;
    }

    private static String getChannelPath(String channel) {
        return FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.ConfigFileNames.CHANNEL_CONFIG;
    }
}
