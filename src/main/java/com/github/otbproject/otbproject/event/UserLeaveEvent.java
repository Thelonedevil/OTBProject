package com.github.otbproject.otbproject.event;

public class UserLeaveEvent extends UserServiceChannelEvent {
    public UserLeaveEvent(String channel, String user) {
        super(channel, user);
    }
}
