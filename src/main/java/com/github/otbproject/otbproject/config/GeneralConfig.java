package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class GeneralConfig {
    private Service service = Service.TWITCH;
    public ArrayList<String> permanently_enabled_commands;

    public GeneralConfig() {
        permanently_enabled_commands = new ArrayList<>();
        permanently_enabled_commands.add("!bot-enable-meta");
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
