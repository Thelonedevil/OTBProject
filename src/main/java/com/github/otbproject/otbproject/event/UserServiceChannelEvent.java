package com.github.otbproject.otbproject.event;

abstract class UserServiceChannelEvent {
    private final String channel;
    private final String user;

    public UserServiceChannelEvent(String channel, String user) {
        this.channel = channel;
        this.user = user;
    }

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }
}
