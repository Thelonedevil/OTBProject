package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.channel.ChannelProxy;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;

public class ChannelMessageEvent {
    private final ChannelProxy channel;
    private final PackagedMessage message;
    private final boolean filtered;

    public ChannelMessageEvent(ChannelProxy channel, PackagedMessage message, boolean filtered) {
        this.channel = channel;
        this.message = message;
        this.filtered = filtered;
    }

    public ChannelProxy getChannel() {
        return channel;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public PackagedMessage getMessage() {
        return message;
    }
}
