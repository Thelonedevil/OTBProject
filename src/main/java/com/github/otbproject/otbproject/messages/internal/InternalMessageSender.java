package com.github.otbproject.otbproject.messages.internal;

import com.github.otbproject.otbproject.App;

import java.util.HashMap;

public class InternalMessageSender {
    public static final String DESTINATION_PREFIX = "internal:";

    public static final String CLI = "cli";

    private final HashMap<String, Runnable> map = new HashMap<>();
    private final String destination;
    private final String message;

    public InternalMessageSender(String destination, String message) {
        map.put(CLI, this::sendToCli);

        this.destination = destination;
        this.message = message;
    }

    public void sendMessage() {
        if (map.containsKey(destination)) {
            map.get(destination).run();
        } else {
            App.logger.error("Invalid internal destination: " + destination);
        }
    }

    private void sendToCli() {
        App.logger.info(message);
    }
}
