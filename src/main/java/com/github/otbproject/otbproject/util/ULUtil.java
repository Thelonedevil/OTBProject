package com.github.otbproject.otbproject.util;


import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.users.Users;

/**
 * Created by Justin on 06/03/2015.
 */
public class ULUtil {

    public static UserLevel getUserLevel(DatabaseWrapper db, String channel, String user) {
        if (user.equalsIgnoreCase(channel)) {
            return UserLevel.BROADCASTER;
        }

        UserLevel ul = null;
        if (Users.exists(db, user)) {
            ul = Users.get(db, user).getUserLevel();
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
        if (APIChannel.get(channel).subscriberStorage.remove(user)) {
            return UserLevel.SUBSCRIBER;
        }

        // Default
        return UserLevel.DEFAULT;
    }
}
