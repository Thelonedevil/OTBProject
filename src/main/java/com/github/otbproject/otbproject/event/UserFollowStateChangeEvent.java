package com.github.otbproject.otbproject.event;

import com.github.otbproject.otbproject.event.state.FollowState;

public class UserFollowStateChangeEvent extends UserServiceChannelEvent {
    private final FollowState followState;

    public UserFollowStateChangeEvent(String channel, String user, FollowState followState) {
        super(channel, user);
        this.followState = followState;
    }

    public FollowState getFollowState() {
        return followState;
    }
}
