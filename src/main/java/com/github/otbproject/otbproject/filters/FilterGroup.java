package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.users.UserLevel;

import javax.validation.constraints.NotNull;

public class FilterGroup {
    @NotNull
    private String name;
    private String responseCommand = "~%filter.response.default";
    private UserLevel userLevel = UserLevel.SUBSCRIBER;
    private FilterAction action = FilterAction.PURGE;
    private Boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(String responseCommand) {
        this.responseCommand = responseCommand;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public FilterAction getAction() {
        return action;
    }

    public void setAction(FilterAction action) {
        this.action = action;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
