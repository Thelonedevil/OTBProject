package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.ChannelNotFoundException;
import com.github.otbproject.otbproject.channels.Channels;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.users.UserLevels;

public class BotUtil {
    public static boolean isModOrHigher(String channelName, String user) throws ChannelNotFoundException {
        // Check if user has user level mod or higher
        Channel channel;
        if (!Channels.in(channelName) || ((channel = Channels.get(channelName)) == null)) {
            throw new ChannelNotFoundException("Not in channel or channel is null.");
        }
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        UserLevel ul = UserLevels.getUserLevel(db, channelName, user);
        return ul.getValue() >= UserLevel.MODERATOR.getValue();
    }
}
