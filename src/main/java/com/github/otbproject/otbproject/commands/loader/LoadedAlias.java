package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.users.UserLevel;

public class LoadedAlias {
    private String name;
    private String command;
    private UserLevel modifyingUserLevel;
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public UserLevel getModifyingUserLevel() {
        return modifyingUserLevel;
    }

    public void setModifyingUserLevel(UserLevel modifyingUserLevel) {
        this.modifyingUserLevel = modifyingUserLevel;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
