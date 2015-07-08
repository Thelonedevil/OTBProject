package com.github.otbproject.otbproject.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GeneralConfig {
    private Service service = Service.TWITCH;
    public List<String> permanently_enabled_commands;

    public GeneralConfig() {
        permanently_enabled_commands = new CopyOnWriteArrayList<>();
        permanently_enabled_commands.add("!bot-enable-meta");
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
