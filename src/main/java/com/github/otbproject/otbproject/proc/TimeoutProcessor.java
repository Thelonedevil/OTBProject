package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.user.UserLevel;

public class TimeoutProcessor {
    public static boolean doTimeouts(PackagedMessage packagedMessage) {
        return false; // TODO implement
    }

    public static ProcessedMessage doTimeouts(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel) {
        return ProcessedMessage.empty(); // TODO implement timeouts
        // Returns empty ProcessedMessage until timeouts are implemented
    }
}
