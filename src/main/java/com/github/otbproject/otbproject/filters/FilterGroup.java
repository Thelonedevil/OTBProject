package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.users.UserLevel;

public class FilterGroup {
    private String name;
    private String responseCommand;
    private UserLevel userLevel;
    private FilterAction action;
    private Boolean enabled;

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
