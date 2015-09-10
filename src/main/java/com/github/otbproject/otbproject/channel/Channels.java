package com.github.otbproject.otbproject.channel;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.StrUtils;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channels {
    private static final Lock lock = new ReentrantLock();

    public static boolean in(String channelName) {
        Optional<Channel> optional = get(channelName);
        return optional.isPresent() && optional.get().isInChannel();
    }

    public static Optional<Channel> get(String channel) {
        return Optional.ofNullable(Control.getBot().getChannels().get(channel));
    }

    public static Channel getOrThrow(String channel) throws ChannelNotFoundException {
        return get(channel).orElseThrow(ChannelNotFoundException::new);
    }

    public static boolean join(String channelName) {
        return join(channelName, JoinCheck.ALL_CHECKS);
    }

    public static boolean join(String channelName, EnumSet<JoinCheck> checks) {
        return doJoin(channelName.toLowerCase(), checks);
    }

    private static boolean doJoin(final String channelName, EnumSet<JoinCheck> checks) {
        App.logger.info("Attempting to join channel: " + channelName);

        lock.lock();
        try {
            if (in(channelName)) {
                App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
                return false;
            }

            Bot bot = Control.getBot();
            boolean isBotChannel = channelName.equals(bot.getUserName());

            // Check if bot is connected
            if (!bot.isConnected()) {
                App.logger.warn("Not connected to " + StrUtils.capitalizeFully(Configs.getFromGeneralConfig(GeneralConfig::getService).toString()));
                return false;
            }

            // Check whitelist/blacklist
            ChannelJoinSetting channelJoinSetting = Configs.getFromBotConfig(BotConfig::getChannelJoinSetting);
            if (!isBotChannel) {
                if (checks.contains(JoinCheck.WHITELIST)
                        && (channelJoinSetting == ChannelJoinSetting.WHITELIST)
                        && !Configs.getFromBotConfig(BotConfig::getWhitelist).contains(channelName)) {
                    App.logger.info("Failed to join channel: " + channelName + ". Not whitelisted.");
                    return false;
                } else if (checks.contains(JoinCheck.BLACKLIST)
                        && (channelJoinSetting == ChannelJoinSetting.BLACKLIST)
                        && Configs.getFromBotConfig(BotConfig::getBlacklist).contains(channelName)) {
                    App.logger.info("Failed to join channel: " + channelName + ". Blacklisted.");
                    return false;
                }
            }

            if (checks.contains(JoinCheck.IS_CHANNEL) && !bot.isChannel(channelName)) {
                App.logger.info("Failed to join channel: " + channelName + ". Channel does not exist.");
                return false;

            }

            // Setup channel files
            try {
                Setup.setupChannel(channelName);
            } catch (IOException e) {
                App.logger.error("Failed to setup channel: " + channelName);
                App.logger.catching(e);
                return false;
            }

            // Connect to channel
            if (!bot.isConnected(channelName)) {
                if (!bot.join(channelName)) {
                    App.logger.warn("Failed to connect to channel: " + channelName);
                    return false;
                }
            } else {
                App.logger.error("Already connected to channel: " + channelName);
            }

            // Create and join channel object
            Optional<Channel> optional = get(channelName);
            Channel channel;
            if (!optional.isPresent()) {
                ChannelConfig channelConfig = Configs.readChannelConfig(channelName);
                try {
                    channel = Channel.create(channelName, channelConfig);
                } catch (ChannelInitException e) {
                    App.logger.catching(e);
                    // Disconnect from channel if failed to create channel object
                    bot.leave(channelName);
                    return false;
                }
                bot.getChannels().put(channelName, channel);
            } else {
                channel = optional.get();
            }
            channel.join();

            // Add channel to list of channels bot is in in config (if not bot channel)
            if (!isBotChannel) {
                Configs.editBotConfig(config -> config.getCurrentChannels().add(channelName));
            }
        } finally {
            lock.unlock();
        }
        App.logger.info("Successfully joined channel: " + channelName);
        return true;
    }

    public static boolean leave(String channelName) {
        final String channel = channelName.toLowerCase();
        lock.lock();
        try {
            if (!in(channel)) {
                App.logger.info("Not leaving channel '" + channel + "' - not in channel");
                return false;
            } else if (channel.equals(Control.getBot().getUserName())) {
                App.logger.info("Not leaving channel '" + channel + "' - cannot leave bot channel");
                return false;
            }
            App.logger.info("Leaving channel: " + channel);
            get(channel).ifPresent(Channel::leave); // TODO possibly remove from channel list?
            Configs.editBotConfig(config -> config.getCurrentChannels().remove(channel));
            Control.getBot().leave(channel);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public static Set<String> list() {
        return Control.getBot().getChannels().keySet();
    }

    public static boolean isBotChannel(String channel) {
        return channel.equalsIgnoreCase(Control.getBot().getUserName());
    }

    public static boolean isBotChannel(Channel channel) {
        return isBotChannel(channel.getName());
    }

}
