package com.github.otbproject.otbproject.event;

public class UserLeaveEvent {
    private final String channel;
    private final String user;

    public UserLeaveEvent(String channel, String user) {
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
