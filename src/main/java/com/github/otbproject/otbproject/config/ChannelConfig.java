package com.github.otbproject.otbproject.config;

public class ChannelConfig {
    private Integer commandCooldown;
    public UserCooldowns userCooldowns;

    public class UserCooldowns {
        private int ul_internal;
        private int ul_broadcaster;
        private int ul_super_moderator;
        private int ul_moderator;
        private Integer ul_regular;
        private Integer ul_subscriber;
        private Integer ul_default;

        public int getUl_internal() {
            return ul_internal;
        }

        public void setUl_internal(int ul_internal) {
            this.ul_internal = ul_internal;
        }

        public int getUl_broadcaster() {
            return ul_broadcaster;
        }

        public void setUl_broadcaster(int ul_broadcaster) {
            this.ul_broadcaster = ul_broadcaster;
        }

        public int getUl_super_moderator() {
            return ul_super_moderator;
        }

        public void setUl_super_moderator(int ul_super_moderator) {
            this.ul_super_moderator = ul_super_moderator;
        }

        public int getUl_moderator() {
            return ul_moderator;
        }

        public void setUl_moderator(int ul_moderator) {
            this.ul_moderator = ul_moderator;
        }

        public Integer getUl_regular() {
            return ul_regular;
        }

        public void setUl_regular(int ul_regular) {
            this.ul_regular = ul_regular;
        }

        public Integer getUl_subscriber() {
            return ul_subscriber;
        }

        public void setUl_subscriber(int ul_subscriber) {
            this.ul_subscriber = ul_subscriber;
        }

        public Integer getUl_default() {
            return ul_default;
        }

        public void setUl_default(int ul_default) {
            this.ul_default = ul_default;
        }
    }

    private boolean debug;

    public Integer getCommandCooldown() {
        return commandCooldown;
    }

    public void setCommandCooldown(int commandCooldown) {
        this.commandCooldown = commandCooldown;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
