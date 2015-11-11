package com.github.otbproject.otbproject.event;

public class ServiceChannelConnectEvent {
    private final String channel;

    public ServiceChannelConnectEvent(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
