package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.users.Users;

public class Util {
    public static UserLevel getUserLevel(DatabaseWrapper db, String channel, String user, boolean subscriber) {
        if (Users.exists(db, user)) {
            return Users.get(db, user).getUserLevel();
        }
        if (subscriber) {
            return UserLevel.SUBSCRIBER;
        }
        if (user.equals(channel)) {
            return UserLevel.BROADCASTER;
        }
        // TODO figure out how to find out if mod

        // Default
        return UserLevel.DEFAULT;
    }
}
