package com.github.otbproject.otbproject.config;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralConfig {
    private Service service = Service.TWITCH;
    //private boolean checkForUpdates = true;
    private Set<String> permanentlyEnabledCommands;

    public GeneralConfig() {
        permanentlyEnabledCommands = ConcurrentHashMap.newKeySet();
        permanentlyEnabledCommands.add("!bot-enable-meta");
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Set<String> getPermanentlyEnabledCommands() {
        return permanentlyEnabledCommands;
    }

    public void setPermanentlyEnabledCommands(List<String> permanentlyEnabledCommands) {
        this.permanentlyEnabledCommands.addAll(permanentlyEnabledCommands);
    }
}
