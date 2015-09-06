package com.github.otbproject.otbproject.channel;

import java.util.EnumSet;

public enum JoinCheck {
    IS_CHANNEL, WHITELIST, BLACKLIST;

    public static final EnumSet<JoinCheck> ALL_CHECKS = EnumSet.allOf(JoinCheck.class);
}
