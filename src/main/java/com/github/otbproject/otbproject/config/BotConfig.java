package com.github.otbproject.otbproject.config;

import java.util.HashSet;

public class BotConfig {
    private ChannelJoinSetting channelJoinSetting;
    private HashSet<String> whitelist;
    private HashSet<String> blacklist;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }

    public HashSet<String> getWhitelist() {
        return whitelist;
    }

    public HashSet<String> getBlacklist() {
        return blacklist;
    }
}
