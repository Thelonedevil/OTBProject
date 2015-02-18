package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.users.UserLevel;

public class DefaultCommandGenerator {

    public static LoadedCommand createDefaultCommand() {
        LoadedCommand command = new LoadedCommand();
        command.setName("!example-command-name");
        command.setResponse("example response");
        command.setExecUserLevel(UserLevel.DEFAULT);
        command.setMinArgs(0);
        command.modifyingUserLevels = command.new ModifyingUserLevels();
        command.modifyingUserLevels.setNameModifyingUL(UserLevel.DEFAULT);
        command.modifyingUserLevels.setResponseModifyingUL(UserLevel.DEFAULT);
        command.modifyingUserLevels.setUserLevelModifyingUL(UserLevel.DEFAULT);
        command.setScript(null);
        command.setEnabled(true);
        command.setDebug(false);

        return command;
    }

    public static LoadedAlias createDefaultAlias() {
        LoadedAlias alias = new LoadedAlias();
        alias.setName("!example-alias-name");
        alias.setCommand("!example command");
        alias.setModifyingUserLevel(UserLevel.DEFAULT);
        alias.setEnabled(true);

        return alias;
    }
}
