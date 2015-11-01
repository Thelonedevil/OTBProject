package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.ChannelProxy;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Configs {

    private static final String GENERAL_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG;
    private static final String WEB_CONFIG_PATH = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.WEB_CONFIG;

    private static String accountFileName = "";
    private static WrappedConfig<Account> account;

    private static final WrappedConfig<GeneralConfig> generalConfig = WrappedConfig.of(GeneralConfig.class, GENERAL_CONFIG_PATH, GeneralConfig::new);
    private static final WrappedConfig<WebConfig> webConfig = WrappedConfig.of(WebConfig.class, WEB_CONFIG_PATH, WebConfig::new);

    private static final UpdatingConfig<BotConfig> botConfig;
    private static final WrappedConfig<BotConfig> botConfigProxy;

    static {
        botConfig = UpdatingConfig.create(BotConfig.class, FSUtil.configDir(), FSUtil.ConfigFileNames.BOT_CONFIG, BotConfig::new);
        botConfig.startMonitoring();
        botConfigProxy = botConfig.asWrappedConfig();
    }

    // Update
    public static void reloadAccount() { // TODO rename
        account = WrappedConfig.of(Account.class, getAccountPath(), Account::new);
    }

    @Deprecated
    public static void updateGeneralConfig() {
        generalConfig.update();
    }

    @Deprecated
    public static void updateWebConfig() {
        webConfig.update();
    }

    // Config getters
    public static WrappedConfig<Account> getAccount() {
        return account;
    }

    public static WrappedConfig<GeneralConfig> getGeneralConfig() {
        return generalConfig;
    }

    public static WrappedConfig<WebConfig> getWebConfig() {
        return webConfig;
    }

    public static WrappedConfig<BotConfig> getBotConfig() {
        return botConfigProxy;
    }

    public static WrappedConfig<ChannelConfig> getChannelConfig(String channel) throws ChannelNotFoundException {
        return Control.getBot().channelManager().getOrThrow(channel).getConfig();
    }

    public static WrappedConfig<ChannelConfig> getChannelConfig(ChannelProxy channel) {
        return channel.getConfig();
    }

    // Edit wrappers
    @Deprecated
    public static void editAccount(Consumer<Account> consumer) {
        account.edit(consumer);
    }

    @Deprecated
    public static void editGeneralConfig(Consumer<GeneralConfig> consumer) {
        generalConfig.edit(consumer);
    }

    @Deprecated
    public static void editWebConfig(Consumer<WebConfig> consumer) {
        webConfig.edit(consumer);
    }

    @Deprecated
    public static void editBotConfig(Consumer<BotConfig> consumer) {
        botConfig.edit(consumer);
    }

    @Deprecated
    public static void editChannelConfig(String channel, Consumer<ChannelConfig> consumer) throws ChannelNotFoundException {
        Control.getBot().channelManager().getOrThrow(channel).editConfig(consumer);
    }

    @Deprecated
    public static void editChannelConfig(ChannelProxy channel, Consumer<ChannelConfig> consumer) {
        channel.editConfig(consumer);
    }

    // Get wrappers
    @Deprecated
    public static <R> R getFromAccount(Function<Account, R> function) {
        return account.get(function);
    }

    @Deprecated
    public static <R> R getFromGeneralConfig(Function<GeneralConfig, R> function) {
        return generalConfig.get(function);
    }

    /**
     * Method to get service if must get exact service as it is in
     * the config, and cannot be out of date.
     *
     * Method is slow and inefficient, and should only be used if
     * exact value is required.
     *
     * @return {@link Service} currently set in {@link GeneralConfig}
     * @throws ExecutionException if encountered Exception when
     * getting the value
     * @throws InterruptedException if thread was interrupted
     * when getting the value
     */
    @Deprecated
    public static Service getExactService() throws ExecutionException, InterruptedException {
        return generalConfig.get(GeneralConfig::getService);
    }

    /**
     * Method to get service if must get exact service as it is in
     * the config, and cannot be out of date.
     *
     * Method is slow and inefficient, and should only be used if
     * exact value is required.
     *
     * @return {@link Optional} containing the {@link Service}, or
     * an empty {@code Optional} if it encountered an Exception or
     * was interrupted
     */
    @Deprecated
    public static Optional<Service> getExactServiceAsOptional() {
        try {
            return Optional.of(getExactService());
        } catch (InterruptedException e) {
            App.logger.catching(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            App.logger.catching(e);
        }
        return Optional.empty();
    }

    @Deprecated
    public static <R> R getFromWebConfig(Function<WebConfig, R> function) {
        return webConfig.get(function);
    }

    @Deprecated
    public static <R> R getFromBotConfig(Function<BotConfig, R> function) {
        return botConfig.get(function);
    }

    @Deprecated
    public static <R> R getFromChannelConfig(String channel, Function<ChannelConfig, R> function) throws ChannelNotFoundException {
        return Control.getBot().channelManager().getOrThrow(channel).getFromConfig(function);
    }

    @Deprecated
    public static <R> R getFromChannelConfig(ChannelProxy channel, Function<ChannelConfig, R> function) {
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
