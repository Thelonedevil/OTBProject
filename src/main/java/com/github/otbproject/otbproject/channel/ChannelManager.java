package com.github.otbproject.otbproject.channel;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.StrUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ChannelManager {
    private final Lock lock = new ReentrantLock();
    private final ConcurrentMap<String, ProxiedChannel> channels;

    public ChannelManager(ConcurrentMap<String, ProxiedChannel> channels) {
        this.channels = channels;
    }

    public boolean in(String channelName) {
        Optional<ChannelProxy> optional = get(channelName);
        return optional.isPresent() && optional.get().isInChannel();
    }

    public Optional<ChannelProxy> get(String channel) {
        return Optional.ofNullable(channels.get(channel).proxy());
    }

    public ChannelProxy getOrThrow(String channel) throws ChannelNotFoundException {
        return get(channel).orElseThrow(ChannelNotFoundException::new);
    }

    public boolean join(String channelName) {
        return join(channelName, JoinCheck.ALL_CHECKS);
    }

    public boolean join(String channelName, EnumSet<JoinCheck> checks) {
        return doJoin(channelName.toLowerCase(), checks);
    }

    private boolean doJoin(final String channelName, EnumSet<JoinCheck> checks) {
        App.logger.info("Attempting to join channel: " + channelName);

        lock.lock();
        try {
            if (in(channelName)) {
                App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
                return false;
            }

            boolean isBotChannel = Channel.isBotChannel(channelName);
            Bot bot = Control.getBot();

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
            ProxiedChannel proxiedChannel = channels.get(channelName);
            Channel channel;
            if (proxiedChannel == null) {
                try {
                    UpdatingConfig<ChannelConfig> updatingConfig = UpdatingConfig.create(ChannelConfig.class,
                            FSUtil.channelDataDir(channelName), FSUtil.ConfigFileNames.CHANNEL_CONFIG, ChannelConfig::new);
                    channel = Channel.create(channelName, updatingConfig);
                } catch (ChannelInitException e) {
                    App.logger.catching(e);
                    // Disconnect from channel if failed to create channel object
                    bot.leave(channelName);
                    return false;
                }
                channels.put(channelName, new ProxiedChannel(channel));
            } else {
                channel = proxiedChannel.channel();
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

    public boolean leave(String channelName) {
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
            Optional.ofNullable(channels.remove(channel)).ifPresent(proxiedChannel -> proxiedChannel.channel().leave());
            Configs.editBotConfig(config -> config.getCurrentChannels().remove(channel));
            Control.getBot().leave(channel);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public Set<String> list() {
        return Collections.unmodifiableSet(channels.keySet());
    }

}
