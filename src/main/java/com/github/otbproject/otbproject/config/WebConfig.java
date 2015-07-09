package com.github.otbproject.otbproject.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebConfig {
    private boolean enabled = true;
    private int portNumber = 22222;
    private String ip_binding = "0.0.0.0";
    public List<String> whitelistedIPAddressesWithSubnettingPrefix;

    public WebConfig() {
        whitelistedIPAddressesWithSubnettingPrefix = new CopyOnWriteArrayList<>(Arrays.asList("127.0.0.0/8", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16"));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
