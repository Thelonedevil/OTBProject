package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.commands.parser.CommandResponseParser;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;

public class CommandProcessor {
    public static ProcessedCommand process(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        String commandMsg = checkAlias(db, message, "");
        return checkCommand(db, commandMsg, channel, user, userLevel, debug);
    }

    public static String checkAlias(DatabaseWrapper db, String message, String originalAlias) {
        String[] splitMsg = message.split(" ", 2);
        String aliasName = splitMsg[0];

        if (originalAlias.equals("")) {
            originalAlias = aliasName;
        }
        // Prevent infinite alias loop
        else if (aliasName.equals(originalAlias)) {
            return message;
        }
        if (Alias.exists(db, aliasName)) {
            LoadedAlias loadedAlias = Alias.get(db, aliasName);
            if (loadedAlias.isEnabled()) {
                if (splitMsg.length == 1) {
                    return checkAlias(db, loadedAlias.getCommand(), originalAlias);
                }
                return checkAlias(db, (loadedAlias.getCommand() + " " + splitMsg[1]), originalAlias);
            }
        }
        // Return message if not an alias
        return message;
    }

    // Returns an empty string if script command
    private static ProcessedCommand checkCommand(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        String[] splitMsg = message.split(" ", 2);
        String cmdName = splitMsg[0];

        String[] args;
        if (splitMsg.length == 1) {
            args = new String[0];
        } else {
            args = splitMsg[1].split(" ");
        }

        if (Command.exists(db, cmdName)) {
            LoadedCommand loadedCommand = Command.get(db, cmdName);
            if (loadedCommand.isEnabled() && userLevel.getValue() >= loadedCommand.getExecUserLevel().getValue() && args.length >= loadedCommand.getMinArgs()) {
                String scriptPath = loadedCommand.getScript();
                // Return script path
                if ((scriptPath != null) && !scriptPath.equals("null")) {
                    return new ProcessedCommand(scriptPath, cmdName, true, args);
                }
                // Else non-script command
                // Check if command is debug
                else if (!loadedCommand.isDebug() || debug) {
                    String response = CommandResponseParser.parse(user, channel, (loadedCommand.getCount() + 1), args, loadedCommand.getResponse());
                    return new ProcessedCommand(response, cmdName, false, args);
                }
            }
        }
        return new ProcessedCommand("", "", false, args);
    }
}
