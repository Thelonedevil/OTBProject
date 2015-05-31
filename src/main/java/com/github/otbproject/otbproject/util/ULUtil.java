package com.github.otbproject.otbproject.util;


import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.users.User;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.users.Users;

public class ULUtil {

    public static UserLevel getUserLevel(DatabaseWrapper db, String channel, String user) {
        if (user.startsWith(InternalMessageSender.DESTINATION_PREFIX)) {
            return UserLevel.INTERNAL;
        }

        if (user.equalsIgnoreCase(channel)) {
            return UserLevel.BROADCASTER;
        }

        UserLevel ul = null;
        User userObj = Users.get(db, user);
        if (userObj != null) {
            ul = userObj.getUserLevel();
        }

        if (ul == UserLevel.SUPER_MODERATOR) {
            return ul;
        }
        if (APIBot.getBot().isUserMod(channel, user)) {
            return UserLevel.MODERATOR;
        }
        if ((ul == UserLevel.REGULAR) || ul == UserLevel.IGNORED) {
            return ul;
        }
        if (APIBot.getBot().isUserSubscriber(channel, user)) {
            return UserLevel.SUBSCRIBER;
        }

        // Default
        return UserLevel.DEFAULT;
    }
}
