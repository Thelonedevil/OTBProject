package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.bot.ChannelState;

public class ChannelStateChangeEvent {
    private final ChannelState newState;

    public ChannelStateChangeEvent(ChannelState newState) {
        this.newState = newState;
    }

    public ChannelState getNewState() {
        return newState;
    }
}
