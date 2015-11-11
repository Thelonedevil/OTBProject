package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.event.state.SubscriptionState;

public class UserSubscriptionStateChangeEvent extends UserServiceChannelEvent {
    private final SubscriptionState subscriptionState;

    public UserSubscriptionStateChangeEvent(String channel, String user, SubscriptionState subscriptionState) {
        super(channel, user);
        this.subscriptionState = subscriptionState;
    }

    public SubscriptionState getSubscriptionState() {
        return subscriptionState;
    }
}
