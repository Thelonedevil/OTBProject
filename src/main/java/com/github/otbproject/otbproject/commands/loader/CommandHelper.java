package com.github.otbproject.otbproject.commands.loader;

public class CommandHelper {
    public static Command getCopy(Command command) {
        Command copy = new Command();

        copy.setName(command.getName());
        copy.setResponse(command.getResponse());
        copy.setExecUserLevel(command.getExecUserLevel());
        copy.setMinArgs(command.getMinArgs());
        copy.modifyingUserLevels = copy.new ModifyingUserLevels();
        if (command.modifyingUserLevels == null) {
            command.modifyingUserLevels = command.new ModifyingUserLevels();
        }
        copy.modifyingUserLevels.setNameModifyingUL(command.modifyingUserLevels.getNameModifyingUL());
        copy.modifyingUserLevels.setResponseModifyingUL(command.modifyingUserLevels.getResponseModifyingUL());
        copy.modifyingUserLevels.setUserLevelModifyingUL(command.modifyingUserLevels.getUserLevelModifyingUL());
        copy.setScript(command.getScript());
        copy.setEnabled(command.isEnabled());
        copy.setDebug(command.isDebug());

        return copy;
    }

    public static LoadedAlias getCopy(LoadedAlias alias) {
        LoadedAlias copy = new LoadedAlias();

        copy.setName(alias.getName());
        copy.setCommand(alias.getCommand());
        copy.setModifyingUserLevel(alias.getModifyingUserLevel());
        copy.setEnabled(alias.isEnabled());

        return copy;
    }
}
