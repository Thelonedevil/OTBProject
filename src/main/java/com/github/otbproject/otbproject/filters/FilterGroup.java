package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.users.UserLevel;

public class FilterGroup {
    private String name;
    private String responseCommand;
    private UserLevel userLevel;

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
}
