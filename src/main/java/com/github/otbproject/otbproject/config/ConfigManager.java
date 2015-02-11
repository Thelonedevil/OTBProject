package com.github.otbproject.otbproject.config;

import java.util.HashMap;

public class ConfigManager {
    private GeneralConfig generalConfig;
    private BotConfig botConfig;
    private final HashMap<String, ChannelConfig> channelConfigs;

    public ConfigManager() {
        this.channelConfigs = new HashMap<String, ChannelConfig>();
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public BotConfig getBotConfig() {
        return botConfig;
    }

    public void setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public ChannelConfig putChannelConfig(String channel, ChannelConfig config) {
        return channelConfigs.put(channel, config);
    }

    public ChannelConfig removeChannelConfig(String channel) {
        return channelConfigs.remove(channel);
    }

    public ChannelConfig getChannelConfig(String channel) {
        return channelConfigs.get(channel);
    }

    public void clearChannelConfigs() {
        channelConfigs.clear();
    }
}
