package com.github.otbproject.otbproject.event;

public class ServiceChannelDisconnectEvent {
    private final String channel;

    public ServiceChannelDisconnectEvent(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
