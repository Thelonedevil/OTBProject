package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class BotConfig {
    public ArrayList<String> whitelist;
    public ArrayList<String> blacklist;
    public ArrayList<String> currentChannels;
    private ChannelJoinSetting channelJoinSetting;
    private Integer messageSendDelayInMilliseconds;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }

    public Integer getMessageSendDelayInMilliseconds() {
        return messageSendDelayInMilliseconds;
    }

    public void setMessageSendDelayInMilliseconds(Integer messageSendDelayInMilliseconds) {
        this.messageSendDelayInMilliseconds = messageSendDelayInMilliseconds;
    }
}
