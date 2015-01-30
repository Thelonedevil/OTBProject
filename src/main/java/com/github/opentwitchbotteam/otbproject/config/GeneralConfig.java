package com.github.opentwitchbotteam.otbproject.config;

import java.util.HashSet;

public class GeneralConfig implements IConfig {
    private HashSet<String> channelsJoined;

    public HashSet<String> getChannelsJoined() {
        return channelsJoined;
    }
}
