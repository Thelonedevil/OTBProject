package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class BotConfig {
    private ChannelJoinSetting channelJoinSetting = ChannelJoinSetting.NONE;
    public ArrayList<String> whitelist = new ArrayList<>();
    public ArrayList<String> blacklist = new ArrayList<>();
    public ArrayList<String> currentChannels = new ArrayList<>();
    private Integer messageSendDelayInMilliseconds = 1600;
    private boolean botChannelDebug = true;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }

    public boolean isBotChannelDebug() {
        return botChannelDebug;
    }

    public void setBotChannelDebug(boolean botChannelDebug) {
        this.botChannelDebug = botChannelDebug;
    }

    public Integer getMessageSendDelayInMilliseconds() {
        return messageSendDelayInMilliseconds;
    }

    public void setMessageSendDelayInMilliseconds(Integer messageSendDelayInMilliseconds) {
        this.messageSendDelayInMilliseconds = messageSendDelayInMilliseconds;
    }
}
