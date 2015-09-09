package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.user.UserLevel;

public class ChannelConfigHelper {
    public static int getCooldown(Channel channel, UserLevel ul) {
        switch (ul) {
            case DEFAULT:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_default());
            case SUBSCRIBER:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_subscriber());
            case REGULAR:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_regular());
            case MODERATOR:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_moderator());
            case SUPER_MODERATOR:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_super_moderator());
            case BROADCASTER:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_broadcaster());
            case INTERNAL:
                return channel.getFromConfig(config -> config.userCooldowns.getUl_internal());
            case TOO_HIGH:
            default:
                return 0;
        }
    }
}
