package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.ChannelInitException;
import com.github.otbproject.otbproject.commands.parser.ResponseParserUtil;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.BotConfigHelper;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.config.ChannelJoinSetting;
import com.github.otbproject.otbproject.fs.Setup;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channels {
    private static final Lock lock = new ReentrantLock();

    public static boolean in(String channelName) {
        Channel channel = get(channelName);
        return (channel != null) && channel.isInChannel();
    }
    
    public static Channel get(String channel) {
        return Bot.getBot().getChannels().get(channel);
    }

    public static boolean join(String channelName) {
        return join(channelName, true);
    }

    public static boolean join(String channelName, boolean checkValidChannel) {
        channelName = channelName.toLowerCase();
        App.logger.info("Attempting to join channel: " + channelName);
        boolean isBotChannel;
        BotConfig botConfig;

        lock.lock();
        try {
            if(in(channelName)){
                App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
                return false;
            }

            isBotChannel = channelName.equals(Bot.getBot().getUserName());

            // Check whitelist/blacklist
            botConfig = Configs.getBotConfig();
            ChannelJoinSetting channelJoinSetting = botConfig.getChannelJoinSetting();
            if (!isBotChannel) {
                if (channelJoinSetting == ChannelJoinSetting.WHITELIST) {
                    if (!BotConfigHelper.isWhitelisted(botConfig, channelName)) {
                        App.logger.info("Failed to join channel: " + channelName + ". Not whitelisted.");
                        return false;
                    }
                } else if (channelJoinSetting == ChannelJoinSetting.BLACKLIST) {
                    if (BotConfigHelper.isBlacklisted(botConfig, channelName)) {
                        App.logger.info("Failed to join channel: " + channelName + ". Blacklisted.");
                        return false;
                    }
                }
            }

            if (checkValidChannel && !Bot.getBot().isChannel(channelName)) {
               App.logger.info("Failed to join channel: " + channelName + ". Channel does not exist.");
               return false;

            }

            try {
                Setup.setupChannel(channelName);
            } catch (IOException e) {
                App.logger.error("Failed to setup channel: " + channelName);
                App.logger.catching(e);
                return false;
            }
            if(Bot.getBot().isConnected()) {
                if(!Bot.getBot().isConnected(channelName)) {
                    if (!Bot.getBot().join(channelName)) {
                        App.logger.warn("Failed to join channel: " + channelName);
                        return false;
                    }
                }else{
                    App.logger.error("Already in the channel: "+ channelName);
                }
            } else{
                App.logger.error("Not connected to " + ResponseParserUtil.wordCap(Configs.getGeneralConfig().getServiceName().toString(), true));
                return false;
            }
            Channel channel;
            if (!Bot.getBot().getChannels().containsKey(channelName)) {
                ChannelConfig channelConfig = Configs.readChannelConfig(channelName);
                try {
                    channel = Channel.create(channelName, channelConfig);
                } catch (ChannelInitException e) {
                    App.logger.catching(e);
                    return false;
                }
                Bot.getBot().getChannels().put(channelName, channel);
            } else {
                channel = get(channelName);
            }
            channel.join();

            if (!isBotChannel) {
                BotConfigHelper.addToCurrentChannels(botConfig, channelName);
                Configs.writeBotConfig();
            }
        } finally {
            lock.unlock();
        }
        App.logger.info("Successfully joined channel: "+channelName);
        return true;
    }

    public static boolean leave(String channelName) {
        channelName = channelName.toLowerCase();
        lock.lock();
        try {
            if (!in(channelName) || channelName.equals(Bot.getBot().getUserName())) {
                App.logger.debug("In channel: " + in(channelName));
                App.logger.debug("Bot channel: " + channelName.equals(Bot.getBot().getUserName()));
                return false;
            }
            App.logger.info("Leaving channel: " + channelName);
            get(channelName).leave();
            BotConfigHelper.removeFromCurrentChannels(Configs.getBotConfig(), channelName);
            Configs.writeBotConfig();
            Bot.getBot().leave(channelName);
        } finally {
            lock.unlock();
        }
        return true;
    }
}
