package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

public class Configs {
    private static String accountFileName = "";

    public static final String GENERAL_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG;
    public static final String BOT_CONFIG_PATH = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.ConfigFileNames.BOT_CONFIG;
    public static final String WEB_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.WEB_CONFIG;

    // Reading
    public static Account readAccount() {
        Account account = JsonHandler.readValue(getAccountPath(), Account.class).orElse(new Account());
        writeAccount(account);
        return account;
    }

    public static WebConfig readWebConfig() {
        WebConfig config = JsonHandler.readValue(WEB_CONFIG_PATH, WebConfig.class).orElse(new WebConfig());
        writeWebConfig(config);
        return config;
    }

    public static GeneralConfig readGeneralConfig() {
        GeneralConfig config = JsonHandler.readValue(GENERAL_CONFIG_PATH, GeneralConfig.class).orElse(new GeneralConfig());
        writeGeneralConfig(config);
        return config;
    }

    public static BotConfig readBotConfig() {
        BotConfig config = JsonHandler.readValue(BOT_CONFIG_PATH, BotConfig.class).orElse(new BotConfig());
        writeBotConfig(config);
        return config;
    }

    public static ChannelConfig readChannelConfig(String channel) {
        ChannelConfig config = JsonHandler.readValue(getChannelPath(channel), ChannelConfig.class).orElse(new ChannelConfig());
        writeChannelConfig(config, channel);
        return config;
    }

    // Writing
    private static void writeAccount(Account account) {
        JsonHandler.writeValue(getAccountPath(), account);
    }

    @Deprecated
    public static void writeAccount() {
        writeAccount(getAccount());
    }

    private static void writeGeneralConfig(GeneralConfig config) {
        JsonHandler.writeValue(GENERAL_CONFIG_PATH, config);
    }

    @Deprecated
    public static void writeGeneralConfig() {
        writeGeneralConfig(getGeneralConfig());
    }

    private static void writeWebConfig(WebConfig config) {
        JsonHandler.writeValue(WEB_CONFIG_PATH, config);
    }

    @Deprecated
    public static void writeWebConfig() {
        writeWebConfig(getWebConfig());
    }

    private static void writeBotConfig(BotConfig config) {
        JsonHandler.writeValue(BOT_CONFIG_PATH, config);
    }

    @Deprecated
    public static void writeBotConfig() {
        writeBotConfig(getBotConfig());
    }

    private static void writeChannelConfig(ChannelConfig config, String channel) {
        JsonHandler.writeValue(getChannelPath(channel), config);
    }

    @Deprecated
    public static void writeChannelConfig(String channel) throws ChannelNotFoundException {
        writeChannelConfig(getChannelConfig(channel), channel);
    }

    // Edit wrappers
    public static void editAccount(Consumer<Account> consumer) {
        consumer.accept(getAccount());
        writeAccount(getAccount());
    }

    public static void editGeneralConfig(Consumer<GeneralConfig> consumer) {
        consumer.accept(getGeneralConfig());
        writeGeneralConfig(getGeneralConfig());
    }

    public static void editWebConfig(Consumer<WebConfig> consumer) {
        consumer.accept(getWebConfig());
        writeWebConfig(getWebConfig());
    }

    public static void editBotConfig(Consumer<BotConfig> consumer) {
        consumer.accept(getBotConfig());
        writeBotConfig(getBotConfig());
    }

    public static void editChannelConfig(String channel, Consumer<ChannelConfig> consumer) throws ChannelNotFoundException {
        consumer.accept(getChannelConfig(channel));
        writeChannelConfig(getChannelConfig(channel), channel);
    }

    // Get wrappers
    public static <R> R getFromAccount(Function<Account, R> function) {
        return function.apply(getAccount());
    }

    public static <R> R getFromGeneralConfig(Function<GeneralConfig, R> function) {
        return function.apply(getGeneralConfig());
    }

    public static <R> R getFromWebConfig(Function<WebConfig, R> function) {
        return function.apply(getWebConfig());
    }

    public static <R> R getFromBotConfig(Function<BotConfig, R> function) {
        return function.apply(getBotConfig());
    }

    public static <R> R getFromChannelConfig(String channel, Function<ChannelConfig, R> function) throws ChannelNotFoundException {
        return function.apply(getChannelConfig(channel));
    }

    // Getting
    public static Account getAccount() {
        return App.configManager.getAccount();
    }

    public static GeneralConfig getGeneralConfig() {
        return App.configManager.getGeneralConfig();
    }

    public static WebConfig getWebConfig() {
        return App.configManager.getWebConfig();
    }

    public static BotConfig getBotConfig() {
        return App.configManager.getBotConfig();
    }

    public static ChannelConfig getChannelConfig(String channel) throws ChannelNotFoundException {
        return Channels.getOrThrow(channel).getConfig();
    }

    // Misc
    private static String getAccountFileName() {
        if (!accountFileName.equals("")) {
            return accountFileName;
        }

        Service service = getGeneralConfig().getService();
        if (service == Service.BEAM) {
            return FSUtil.ConfigFileNames.ACCOUNT_BEAM;
        } else { // Defaults to Twitch
            return FSUtil.ConfigFileNames.ACCOUNT_TWITCH;
        }
    }

    public static void setAccountFileName(String accountFileName) {
        Configs.accountFileName = accountFileName;
    }

    private static String getAccountPath() {
        return FSUtil.configDir() + File.separator + getAccountFileName();
    }

    private static String getChannelPath(String channel) {
        return FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.ConfigFileNames.CHANNEL_CONFIG;
    }
}
