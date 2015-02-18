package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class BotConfig {
    private ChannelJoinSetting channelJoinSetting;
    public ArrayList<String> whitelist;
    public ArrayList<String> blacklist;
    public ArrayList<String> currentChannels;

    public ChannelJoinSetting getChannelJoinSetting() {
        return channelJoinSetting;
    }

    public void setChannelJoinSetting(ChannelJoinSetting channelJoinSetting) {
        this.channelJoinSetting = channelJoinSetting;
    }
}
