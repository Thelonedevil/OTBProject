package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.channel.Channel;

public class ChannelLeaveEvent {
    private final Channel channel;

    public ChannelLeaveEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
