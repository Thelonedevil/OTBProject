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

    public BotConfig getCopy() {
        BotConfig copy = new BotConfig();

        copy.channelJoinSetting = this.channelJoinSetting;

        if (this.whitelist == null) {
            copy.whitelist = null;
        }
        else {
            copy.whitelist = new ArrayList<String>(this.whitelist);
        }

        if (this.blacklist == null) {
            copy.blacklist = null;
        }
        else {
            copy.blacklist = new ArrayList<String>(this.blacklist);
        }

        if (this.currentChannels == null) {
            copy.currentChannels = null;
        }
        else {
            copy.currentChannels = new ArrayList<String>(this.currentChannels);
        }

        return copy;
    }
}
