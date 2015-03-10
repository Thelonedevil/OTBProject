package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.serviceapi.ApiRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class APIChannel {
    public static boolean in(String channel) {
        return App.bot.channels.containsKey(channel);
    }
    
    public static Channel get(String channel) {
        return App.bot.channels.get(channel);
    }

    public static boolean join(String channelName) {
        return join(channelName, true);
    }

    public static boolean join(String channelName, boolean checkValidChannel) {
        if(in(channelName)){
            App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
            return false;
        }

        // Check whitelist/blacklist
        BotConfig botConfig = App.bot.configManager.getBotConfig();
        ChannelJoinSetting channelJoinSetting = botConfig.getChannelJoinSetting();
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

        if (checkValidChannel) {
            File channelsDir = new File(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS);
            // Checks that directory exists (so no null pointer), and if channel is already set up
            // If not, does API call to check if channel is valid
            if ( (  !channelsDir.isDirectory()
                    || !(new ArrayList<String>(Arrays.asList(channelsDir.list())).contains(channelName)) )
                    && (ApiRequest.attemptRequest("channels/" + channelName, 3, 500) == null)   ) {
                App.logger.info("Failed to join channel: " + channelName + ". Channel does not exist.");
                return false;
            }
        }

        try {
            Setup.setupChannel(channelName);
        } catch (IOException e) {
            App.logger.error("Failed to setup channel: " + channelName);
            App.logger.catching(e);
            return false;
        }
        if(App.bot.isConnected()) {
            App.bot.sendIRC().joinChannel("#"+channelName);
        } else{
            App.logger.error("Not connected to Twitch");
            return false;
        }
        ChannelConfig channelConfig = APIConfig.readChannelConfig(channelName);
        Channel channel = new Channel(channelName, channelConfig);
        channel.join();
        App.bot.channels.put(channelName, channel);
        BotConfigHelper.addToCurrentChannels(botConfig, channelName);
        APIConfig.writeBotConfig();
        return true;
    }

    public static void leave(String channelName) {
        App.bot.channels.remove(channelName).leave();
        BotConfigHelper.removeFromCurrentChannels(App.bot.configManager.getBotConfig(), channelName);
        APIConfig.writeBotConfig();
        App.bot.getUserChannelDao().getChannel("#"+channelName).send().part();
    }
}
