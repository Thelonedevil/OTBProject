package com.github.otbproject.otbproject.messages.internal;

import com.github.otbproject.otbproject.App;

public class InternalMessageSender {
    public static final String DESTINATION_PREFIX = "internal:";

    public static final String CLI = "cli";


    public static void send(String destination, String message) {
        switch (destination) {
            case CLI:
                sendToCli(message);
                break;
        }
    }

    private static void sendToCli(String message) {
        App.logger.info(message);
    }
}
