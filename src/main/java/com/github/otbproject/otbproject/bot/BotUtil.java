package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelGetException;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.user.UserLevel;
import com.github.otbproject.otbproject.user.UserLevels;

import java.util.Optional;

public class BotUtil {
    public static boolean isModOrHigher(String channelName, String user) throws ChannelGetException {
        // Check if user has user level mod or higher
        Optional<Channel> optional = Channels.get(channelName);
        if (!optional.isPresent()) {
            throw new ChannelGetException("Unable to get Channel '" + channelName + "' to determine user level");
        }
        DatabaseWrapper db = optional.get().getMainDatabaseWrapper();
        UserLevel ul = UserLevels.getUserLevel(db, channelName, user);
        return ul.getValue() >= UserLevel.MODERATOR.getValue();
    }
}
