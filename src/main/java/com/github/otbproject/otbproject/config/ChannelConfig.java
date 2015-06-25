package com.github.otbproject.otbproject.config;

public class ChannelConfig {
    private int commandCooldown = 8;
    public UserCooldowns userCooldowns = new UserCooldowns();

    public class UserCooldowns {
        private int ul_internal = 0;
        private int ul_broadcaster = 0;
        private int ul_super_moderator = 0;
        private int ul_moderator = 0;
        private int ul_regular = 15;
        private int ul_subscriber = 30;
        private int ul_default = 30;

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

    private boolean debug = false;
    private boolean enabled = true;
    private boolean silenced = false;

    public QueueLimits queueLimits = new QueueLimits();

    public class QueueLimits {
        private int highPriorityLimit = -1;
        private int defaultPriorityLimit = 5;
        private int lowPriorityLimit = 0;

        public int getHighPriorityLimit() {
            return highPriorityLimit;
        }

        public void setHighPriorityLimit(int highPriorityLimit) {
            this.highPriorityLimit = highPriorityLimit;
        }

        public int getDefaultPriorityLimit() {
            return defaultPriorityLimit;
        }

        public void setDefaultPriorityLimit(int defaultPriorityLimit) {
            this.defaultPriorityLimit = defaultPriorityLimit;
        }

        public int getLowPriorityLimit() {
            return lowPriorityLimit;
        }

        public void setLowPriorityLimit(int lowPriorityLimit) {
            this.lowPriorityLimit = lowPriorityLimit;
        }
    }

    public int getCommandCooldown() {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }
}
