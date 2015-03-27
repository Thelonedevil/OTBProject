package com.github.otbproject.otbproject.irc;

import com.github.otbproject.otbproject.App;

/**
 * Created by cave on 3/27/15.
 */
public class IrcHelper {
    public static void part(String channel) {
        App.bot.getUserChannelDao().getChannel(getIrcChannelName(channel)).send().part();
    }

    public static void join(String channel) {
        App.bot.sendIRC().joinChannel(getIrcChannelName(channel));
    }

    public static String getIrcChannelName(String channel) {
        return "#" + channel;
    }

    public static String getInternalChannelName(String channel) {
        return channel.replace("#", "");
    }
}
