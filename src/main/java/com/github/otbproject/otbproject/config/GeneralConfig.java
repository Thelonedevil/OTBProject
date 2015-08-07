package com.github.otbproject.otbproject.config;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralConfig {
    private Service service = Service.TWITCH;
    private boolean updateChecking = true;
    private boolean deletingOldLogs = true;
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

    public boolean isUpdateChecking() {
        return updateChecking;
    }

    public void setUpdateChecking(boolean updateChecking) {
        this.updateChecking = updateChecking;
    }

    public boolean isDeletingOldLogs() {
        return deletingOldLogs;
    }

    public void setDeletingOldLogs(boolean deletingOldLogs) {
        this.deletingOldLogs = deletingOldLogs;
    }

    public Set<String> getPermanentlyEnabledCommands() {
        return permanentlyEnabledCommands;
    }

    public void setPermanentlyEnabledCommands(List<String> permanentlyEnabledCommands) {
        this.permanentlyEnabledCommands.clear();
        this.permanentlyEnabledCommands.addAll(permanentlyEnabledCommands);
    }
}
