package com.github.otbproject.otbproject.util.compat;

import com.github.otbproject.otbproject.config.Service;

import java.util.ArrayList;

class GeneralConfigOld {
    private Service serviceName = Service.TWITCH;
    private int portNumber = 22222;
    private String ip_binding = "0.0.0.0";
    public ArrayList<String> permanently_enabled_commands;

    public GeneralConfigOld() {
        permanently_enabled_commands = new ArrayList<>();
        permanently_enabled_commands.add("!bot-enable-meta");
    }

    public Service getServiceName() {
        return serviceName;
    }

    public void setServiceName(Service serviceName) {
        this.serviceName = serviceName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getIp_binding() {
        return ip_binding;
    }

    public void setIp_binding(String ip_binding) {
        this.ip_binding = ip_binding;
    }
}
