package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.ChannelNotFoundException;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.util.ULUtil;

public class BotUtil {
    public static boolean isModOrHigher(String channelName, String user) throws ChannelNotFoundException {
        // Check if user has user level mod or higher
        Channel channel;
        if (!APIChannel.in(channelName) || ((channel = APIChannel.get(channelName)) == null)) {
            throw new ChannelNotFoundException("Not in channel or channel is null.");
        }
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        UserLevel ul = ULUtil.getUserLevel(db, channelName, user);
        return ul.getValue() >= UserLevel.MODERATOR.getValue();
    }
}
