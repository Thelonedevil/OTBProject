package com.github.otbproject.otbproject.messages.internal;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.gui.GuiApplication;

public class InternalMessageSender {
    public static final String DESTINATION_PREFIX = "internal:";

    public static final String CLI = "cli";


    public static void send(String destination, String message, String source) {
        switch (destination) {
            case CLI:
                sendToCli(message, source);
                break;
        }
    }

    private static void sendToCli(String message, String source) {
        App.logger.debug("Sending to CIL from <" + source + ">: " + message);
        GuiApplication.addInfo("<" + source + "> " + message);
    }
}
