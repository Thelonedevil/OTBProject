package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.config.Service;

public class BotStopEvent {
    private final Service service;

    public BotStopEvent(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
