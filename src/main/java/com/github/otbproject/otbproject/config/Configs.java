package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

public class Configs {

    private static final String GENERAL_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG;
    private static final String WEB_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.WEB_CONFIG;

    private static String accountFileName = "";
    private static WrappedConfig<Account> account;

    private static final WrappedConfig<GeneralConfig> generalConfig = new WrappedConfig<>(GeneralConfig.class, GENERAL_CONFIG_PATH, GeneralConfig::new);
    private static final WrappedConfig<WebConfig> webConfig = new WrappedConfig<>(WebConfig.class, WEB_CONFIG_PATH, WebConfig::new);

    private static final UpdatingConfig<BotConfig> botConfig;

    static {
        botConfig = UpdatingConfig.create(BotConfig.class, FSUtil.configDir(), FSUtil.ConfigFileNames.BOT_CONFIG, BotConfig::new);
        botConfig.startMonitoring();
    }

    // Update
    public static void reloadAccount() { // TODO rename
        account = new WrappedConfig<>(Account.class, getAccountPath(), Account::new);
    }

    public static void updateGeneralConfig() {
        generalConfig.update();
    }

    public static void updateWebConfig() {
        webConfig.update();
    }

    // Edit wrappers
    public static void editAccount(Consumer<Account> consumer) {
        account.edit(consumer);
    }

    public static void editGeneralConfig(Consumer<GeneralConfig> consumer) {
        generalConfig.edit(consumer);
    }

    public static void editWebConfig(Consumer<WebConfig> consumer) {
        webConfig.edit(consumer);
    }

    public static void editBotConfig(Consumer<BotConfig> consumer) {
        botConfig.edit(consumer);
    }

    public static void editChannelConfig(String channel, Consumer<ChannelConfig> consumer) throws ChannelNotFoundException {
        Channels.getOrThrow(channel).editConfig(consumer);
    }

    public static void editChannelConfig(Channel channel, Consumer<ChannelConfig> consumer) {
        channel.editConfig(consumer);
    }

    // Get wrappers
    public static <R> R getFromAccount(Function<Account, R> function) {
        return account.get(function);
    }

    public static <R> R getFromGeneralConfig(Function<GeneralConfig, R> function) {
        return generalConfig.get(function);
    }

    public static <R> R getFromWebConfig(Function<WebConfig, R> function) {
        return webConfig.get(function);
    }

    public static <R> R getFromBotConfig(Function<BotConfig, R> function) {
        return botConfig.get(function);
    }

    public static <R> R getFromChannelConfig(String channel, Function<ChannelConfig, R> function) throws ChannelNotFoundException {
        return Channels.getOrThrow(channel).getFromConfig(function);
    }

    public static <R> R getFromChannelConfig(Channel channel, Function<ChannelConfig, R> function) {
        return channel.getFromConfig(function);
    }

    // Misc
    private static String getAccountFileName() {
        if (!accountFileName.equals("")) {
            return accountFileName;
        }

        Service service = generalConfig.get(GeneralConfig::getService);
        switch (service) {
            case BEAM:
                return FSUtil.ConfigFileNames.ACCOUNT_BEAM;
            case TWITCH:
            default:
                return FSUtil.ConfigFileNames.ACCOUNT_TWITCH;
        }
    }

    public static void setAccountFileName(String accountFileName) {
        Configs.accountFileName = accountFileName;
    }

    private static String getAccountPath() {
        return FSUtil.configDir() + File.separator + getAccountFileName();
    }
}
