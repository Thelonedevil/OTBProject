package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.config.Service;

public class BotStartEvent {
    private final Service service;

    public BotStartEvent(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
