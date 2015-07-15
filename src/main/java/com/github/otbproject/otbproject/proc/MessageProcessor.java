package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.user.UserLevel;

public class MessageProcessor {
    // Assumed to be thread-safe
    public static ProcessedMessage process(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        ProcessedMessage processedMsg = TimeoutProcessor.doTimeouts(db, message, channel, user, userLevel);
        if (!processedMsg.timedOut) {
            // Check for aliases and commands, and get appropriate parsed response
            ProcessedCommand processedCmd = CommandProcessor.process(db, message, channel, user, userLevel, debug);
            processedMsg = new ProcessedMessage(processedCmd, false);
        }
        // If timed out, return empty string
        return processedMsg;
    }
}
