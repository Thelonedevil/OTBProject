package com.github.otbproject.otbproject.config;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BotConfig {
    private ChannelJoinSetting channelJoinSetting = ChannelJoinSetting.WHITELIST;
    private Set<String> whitelist = ConcurrentHashMap.newKeySet();
    private Set<String> blacklist = ConcurrentHashMap.newKeySet();
    private Set<String> currentChannels = ConcurrentHashMap.newKeySet();
    private Integer messageSendDelayInMilliseconds = 1600;
    private boolean botChannelDebug = true;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }

    public Set<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist.clear();
        this.whitelist.addAll(whitelist);
    }

    public Set<String> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist.clear();
        this.blacklist.addAll(blacklist);
    }

    public Set<String> getCurrentChannels() {
        return currentChannels;
    }

    public void setCurrentChannels(List<String> currentChannels) {
        this.currentChannels.clear();
        this.currentChannels.addAll(currentChannels);
    }

    public Integer getMessageSendDelayInMilliseconds() {
        return messageSendDelayInMilliseconds;
    }

    public void setMessageSendDelayInMilliseconds(Integer messageSendDelayInMilliseconds) {
        this.messageSendDelayInMilliseconds = messageSendDelayInMilliseconds;
    }

    public boolean isBotChannelDebug() {
        return botChannelDebug;
    }

    public void setBotChannelDebug(boolean botChannelDebug) {
        this.botChannelDebug = botChannelDebug;
    }
}
