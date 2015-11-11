package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.event.ChannelStateChangeEvent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractServiceChannel {
    protected final AtomicReference<ChannelState> state = new AtomicReference<>(ChannelState.DEAD);

    protected void updateChannelState(ChannelState newState) {
        if (state.getAndSet(newState) != newState) {
            fireChannelStateChangeEvent(newState);
        }
    }

    private void fireChannelStateChangeEvent(ChannelState newState) {
        // TODO implement once events are implemented
        //new ChannelStateChangeEvent(newState);
    }
}
