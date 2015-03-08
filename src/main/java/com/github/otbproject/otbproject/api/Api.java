package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.config.BotConfigHelper;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.config.ConfigValidator;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.io.IOException;

public class Api {
    public static boolean joinChannel(String channelName) {
        if(App.bot.channels.containsKey(channelName)){
            App.logger.info("Failed to join channel: " + channelName + ". Already in channel");
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
            App.bot.sendIRC().joinChannel("#"+channelName);
        } else{
            App.logger.error("Not connected to Twitch");
            return false;
        }
        String channelConfPath = FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channelName + File.separator + "config.json";
        ChannelConfig channelConfig = ConfigValidator.validateChannelConfig(JsonHandler.readValue(channelConfPath, ChannelConfig.class));
        JsonHandler.writeValue(channelConfPath, channelConfig);
        Channel channel = new Channel(channelName, channelConfig);
        channel.join();
        App.bot.channels.put(channelName, channel);
        BotConfigHelper.addToCurrentChannels(App.bot.configManager.getBotConfig(), channelName);
        JsonHandler.writeValue(FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + "bot-config.json", App.bot.configManager.getBotConfig());
        return true;
    }

    public static void leaveChannel(String channelName) {
        App.bot.channels.remove(channelName).leave();
        BotConfigHelper.removeFromCurrentChannels(App.bot.configManager.getBotConfig(), channelName);
        JsonHandler.writeValue(FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + "bot-config.json", App.bot.configManager.getBotConfig());
        App.bot.getUserChannelDao().getChannel("#"+channelName).send().part();
    }
}
