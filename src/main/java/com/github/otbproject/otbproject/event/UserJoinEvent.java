package com.github.otbproject.otbproject.event;

public class UserJoinEvent {
    private final String channel;
    private final String user;

    public UserJoinEvent(String channel, String user) {
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
