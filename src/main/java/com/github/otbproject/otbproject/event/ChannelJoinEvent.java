package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.channel.Channel;

public class ChannelJoinEvent {
    private final Channel channel;

    public ChannelJoinEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
