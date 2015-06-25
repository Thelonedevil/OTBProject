package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.user.UserLevel;

public class ChannelConfigHelper {
    public static int getCooldown(ChannelConfig config, UserLevel ul) {
        switch (ul) {
            case DEFAULT:
                return config.userCooldowns.getUl_default();
            case SUBSCRIBER:
                return config.userCooldowns.getUl_subscriber();
            case REGULAR:
                return config.userCooldowns.getUl_regular();
            case MODERATOR:
                return config.userCooldowns.getUl_moderator();
            case SUPER_MODERATOR:
                return config.userCooldowns.getUl_super_moderator();
            case BROADCASTER:
                return config.userCooldowns.getUl_broadcaster();
            case INTERNAL:
                return config.userCooldowns.getUl_internal();
            case TOO_HIGH:
            default:
                return 0;
        }
    }
}
