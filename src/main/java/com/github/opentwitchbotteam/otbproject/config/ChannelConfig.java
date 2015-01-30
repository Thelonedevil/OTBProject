package com.github.opentwitchbotteam.otbproject.config;

public class ChannelConfig implements IConfig {
    private int commandCooldown;

    public static class UserCooldowns {
        private int ul_broadcaster;
        private int ul_super_moderator;
        private int ul_moderator;
        private int ul_regular;
        private int ul_subscriber;
        private int ul_default;

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

        public int getUl_regular() {
            return ul_regular;
        }

        public void setUl_regular(int ul_regular) {
            this.ul_regular = ul_regular;
        }

        public int getUl_subscriber() {
            return ul_subscriber;
        }

        public void setUl_subscriber(int ul_subscriber) {
            this.ul_subscriber = ul_subscriber;
        }

        public int getUl_default() {
            return ul_default;
        }

        public void setUl_default(int ul_default) {
            this.ul_default = ul_default;
        }
    }

    public int getCommandCooldown() {
        return commandCooldown;
    }

    public void setCommandCooldown(int commandCooldown) {
        this.commandCooldown = commandCooldown;
    }
}
