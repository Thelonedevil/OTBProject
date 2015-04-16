package com.github.otbproject.otbproject.config;

import java.util.ArrayList;

public class GeneralConfig {
    private ServiceName serviceName;
    private int portNumber;
    private String ip_binding;
    public ArrayList<String> permanently_enabled_commands;

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void setServiceName(ServiceName serviceName) {
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
