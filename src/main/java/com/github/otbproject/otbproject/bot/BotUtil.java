package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.util.ULUtil;

public class BotUtil {
    // Assumes true if encounters error and unable to determine user level
    public static boolean isModOrHigher(String channelName, String user) {
        // Check if user has user level mod or higher
        Channel channel = APIChannel.get(channelName);
        if (channel == null) {
            App.logger.error("Failed to get user level for user: Channel object for channel '" + channelName + "' is null.");
            return true;
        }
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        UserLevel ul = ULUtil.getUserLevel(db, channelName, user);
        return ul.getValue() >= UserLevel.MODERATOR.getValue();
    }
}
