package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.users.UserLevel;
import javax.validation.constraints.NotNull;

public class LoadedAlias {
    @NotNull
    private String name;

    @NotNull
    private String command;

    private UserLevel modifyingUserLevel = UserLevel.DEFAULT;
    private Boolean enabled = true;

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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
