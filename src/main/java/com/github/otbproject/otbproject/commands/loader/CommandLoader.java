package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.AliasFields;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.CommandFields;
import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.util.HashMap;

public class CommandLoader {
    public static boolean addCommandFromLoadedCommand(DatabaseWrapper db, LoadedCommand loadedCommand) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(CommandFields.NAME, loadedCommand.getName());
        map.put(CommandFields.RESPONSE, loadedCommand.getResponse());
        map.put(CommandFields.EXEC_USER_LEVEL, loadedCommand.getExecUserLevel().name());
        map.put(CommandFields.MIN_ARGS, String.valueOf(loadedCommand.getMinArgs()));
        map.put(CommandFields.COUNT, String.valueOf(loadedCommand.getCount()));
        map.put(CommandFields.NAME_MODIFYING_UL, loadedCommand.modifyingUserLevels.getNameModifyingUL().name());
        map.put(CommandFields.RESPONSE_MODIFYING_UL, loadedCommand.modifyingUserLevels.getResponseModifyingUL().name());
        map.put(CommandFields.USER_LEVEL_MODIFYING_UL, loadedCommand.modifyingUserLevels.getUserLevelModifyingUL().name());
        map.put(CommandFields.SCRIPT, loadedCommand.getScript());
        map.put(CommandFields.ENABLED, String.valueOf(loadedCommand.isEnabled()));
        map.put(CommandFields.DEBUG, String.valueOf(loadedCommand.isDebug()));

        if (Command.exists(db, loadedCommand.getName())) {
            return Command.update(db, map);
        } else {
            return Command.add(db, map);
        }


    }

    public static boolean addAliasFromLoadedAlias(DatabaseWrapper db, LoadedAlias loadedAlias) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put(AliasFields.NAME, loadedAlias.getName());
        map.put(AliasFields.COMMAND, loadedAlias.getCommand());
        map.put(AliasFields.MODIFYING_UL, loadedAlias.getModifyingUserLevel());
        map.put(AliasFields.ENABLED, true);
        if (Alias.exists(db, loadedAlias.getName())) {
            return Alias.update(db, map);
        } else {
            return Alias.add(db, map);
        }

    }
}
