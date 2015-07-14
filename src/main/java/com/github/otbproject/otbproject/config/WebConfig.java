package com.github.otbproject.otbproject.config;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebConfig {
    private boolean enabled = true;
    private boolean autoUpdating = true;
    private int portNumber = 22222;
    private String ipBinding = "0.0.0.0";
    private Set<String> whitelistedIPAddressesWithSubnettingPrefix = ConcurrentHashMap.newKeySet();

    public WebConfig() {
        whitelistedIPAddressesWithSubnettingPrefix.addAll(Arrays.asList("127.0.0.0/8", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16"));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutoUpdating() {
        return autoUpdating;
    }

    public void setAutoUpdating(boolean autoUpdating) {
        this.autoUpdating = autoUpdating;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getIpBinding() {
        return ipBinding;
    }

    public void setIpBinding(String ipBinding) {
        this.ipBinding = ipBinding;
    }

    public Set<String> getWhitelistedIPAddressesWithSubnettingPrefix() {
        return whitelistedIPAddressesWithSubnettingPrefix;
    }

    public void setWhitelistedIPAddressesWithSubnettingPrefix(List<String> whitelistedIPAddressesWithSubnettingPrefix) {
        this.whitelistedIPAddressesWithSubnettingPrefix.addAll(whitelistedIPAddressesWithSubnettingPrefix);
    }
}
