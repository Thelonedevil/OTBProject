package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class GeneralConfig {
    private int portNumber;
    private String ip_binding;
    public ArrayList<String> permanently_enabled_commands;

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

    public GeneralConfig getCopy() {
        GeneralConfig copy = new GeneralConfig();

        copy.portNumber = this.portNumber;
        copy.ip_binding = this.ip_binding;

        if (this.permanently_enabled_commands == null) {
            copy.permanently_enabled_commands = null;
        }
        else {
            copy.permanently_enabled_commands = new ArrayList<String>(this.permanently_enabled_commands);
        }

        return copy;
    }
}
