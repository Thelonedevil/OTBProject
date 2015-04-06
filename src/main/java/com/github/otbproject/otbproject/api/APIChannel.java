package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.commands.parser.ResponseParserUtil;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.BotConfigHelper;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.config.ChannelJoinSetting;
import com.github.otbproject.otbproject.fs.Setup;

import java.io.IOException;

public class APIChannel {
    public static boolean in(String channel) {
        return App.bot.getChannels().containsKey(channel) && get(channel).isInChannel();
    }
    
    public static Channel get(String channel) {
        return App.bot.getChannels().get(channel);
    }

    public static boolean join(String channelName) {
        return join(channelName, true);
    }

    public static boolean join(String channelName, boolean checkValidChannel) {
        channelName = channelName.toLowerCase();
        App.logger.info("Attempting to join channel: " + channelName);
        if(in(channelName)){
            App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
            return false;
        }

        boolean isBotChannel = channelName.equals(App.bot.getUserName());

        // Check whitelist/blacklist
        BotConfig botConfig = APIConfig.getBotConfig();
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

        if (checkValidChannel && !App.bot.isChannel(channelName)) {
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
        if(App.bot.isConnected()) {
            if(!App.bot.isConnected(channelName)) {
                App.bot.join(channelName);
            }else{
                App.logger.error("Already in the channel: "+ channelName);
            }
        } else{
            App.logger.error("Not connected to " + ResponseParserUtil.wordCap(APIConfig.getGeneralConfig().getServiceName().toString(), true));
            return false;
        }
        Channel channel;
        if (!App.bot.getChannels().containsKey(channelName)) {
            ChannelConfig channelConfig = APIConfig.readChannelConfig(channelName);
            channel = new Channel(channelName, channelConfig);
            App.bot.getChannels().put(channelName, channel);
            App.bot.join(channelName);
        } else {
            channel = get(channelName);
        }
        if (!channel.join()) {
            App.logger.error("Failed to join channel '" + channelName + "' internally. Disconnecting from remote channel.");
            App.bot.leave(channelName);
            return false;
        }
        if (!isBotChannel) {
            BotConfigHelper.addToCurrentChannels(botConfig, channelName);
            APIConfig.writeBotConfig();
        }
        return true;
    }

    public static void leave(String channelName) {
        channelName = channelName.toLowerCase();
        if (!in(channelName) || channelName.equals(App.bot.getUserName())) {
            App.logger.debug("In channel: " + in(channelName));
            App.logger.debug("Bot channel: " + channelName.equals(App.bot.getUserName()));
            return;
        }
        App.logger.info("Leaving channel: " + channelName);
        get(channelName).leave();
        BotConfigHelper.removeFromCurrentChannels(APIConfig.getBotConfig(), channelName);
        APIConfig.writeBotConfig();
        App.bot.leave(channelName);
    }
}
