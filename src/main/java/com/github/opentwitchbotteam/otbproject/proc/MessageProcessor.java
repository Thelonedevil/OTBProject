package com.github.opentwitchbotteam.otbproject.proc;

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;

public class MessageProcessor {
    public static void process(DatabaseWrapper db, String message, String execChannel, String targetChannel, String user, boolean subscriber) {
        if (!TimeoutProcessor.doTimeouts(db, message, execChannel, user, subscriber)) {
            CommandProcessor.processCommand(db, message, execChannel, targetChannel, user, subscriber);
        }
    }
}
