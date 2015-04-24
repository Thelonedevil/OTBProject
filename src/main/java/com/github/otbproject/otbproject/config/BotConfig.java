package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class BotConfig {
    private ChannelJoinSetting channelJoinSetting;
    public ArrayList<String> whitelist;
    public ArrayList<String> blacklist;
    public ArrayList<String> currentChannels;
    private Integer messageSendDelayInMilliseconds;
    private Boolean botChannelDebug;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }

    public Boolean isBotChannelDebug() {
        return botChannelDebug;
    }

    public void setBotChannelDebug(Boolean botChannelDebug) {
        this.botChannelDebug = botChannelDebug;
    }

    public Integer getMessageSendDelayInMilliseconds() {
        return messageSendDelayInMilliseconds;
    }

    public void setMessageSendDelayInMilliseconds(Integer messageSendDelayInMilliseconds) {
        this.messageSendDelayInMilliseconds = messageSendDelayInMilliseconds;
    }
}
