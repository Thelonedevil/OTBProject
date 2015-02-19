package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.users.UserLevel;

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
            case INTERNAL:
                return config.userCooldowns.getUl_broadcaster();
            case TOO_HIGH:
            default:
                return 0;
        }
    }

    public static ChannelConfig getCopy(ChannelConfig config) {
        ChannelConfig copy = new ChannelConfig();

        copy.setCommandCooldown(config.getCommandCooldown());
        copy.userCooldowns = copy.new UserCooldowns();
        copy.userCooldowns.setUl_broadcaster(config.userCooldowns.getUl_broadcaster());
        copy.userCooldowns.setUl_super_moderator(config.userCooldowns.getUl_super_moderator());
        copy.userCooldowns.setUl_moderator(config.userCooldowns.getUl_moderator());
        copy.userCooldowns.setUl_regular(config.userCooldowns.getUl_regular());
        copy.userCooldowns.setUl_subscriber(config.userCooldowns.getUl_subscriber());
        copy.userCooldowns.setUl_default(config.userCooldowns.getUl_default());
        copy.setDebug(config.isDebug());
        copy.setEnabled(config.isEnabled());
        return copy;
    }
}
