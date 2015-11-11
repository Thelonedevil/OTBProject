package com.github.otbproject.otbproject.event;

public class UserJoinEvent extends UserServiceChannelEvent {
    public UserJoinEvent(String channel, String user) {
        super(channel, user);
    }
}
