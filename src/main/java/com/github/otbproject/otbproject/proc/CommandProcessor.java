package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.AliasFields;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.CommandFields;
import com.github.otbproject.otbproject.commands.parser.CommandResponseParser;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;

import java.sql.SQLException;

public class CommandProcessor {
    public static ProcessedCommand process(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        String commandMsg = checkAlias(db, message, "");
        return checkCommand(db, commandMsg, channel, user, userLevel, debug);
    }

    private static String checkAlias(DatabaseWrapper db, String message, String originalAlias) {
        String[] splitMsg = message.split(" ", 2);
        String aliasName = splitMsg[0];

        if (originalAlias.equals("")) {
            originalAlias = aliasName;
        }
        // Prevent infinite alias loop
        else if (aliasName.equals(originalAlias)) {
            return message;
        }

        try {
            if (Alias.exists(db, aliasName) && ((Integer)Alias.get(db, aliasName, AliasFields.ENABLED) == 1)) {
                if (splitMsg.length == 1) {
                    return checkAlias(db, (String)Alias.get(db, aliasName, AliasFields.COMMAND), originalAlias);
                }
                return checkAlias(db, ((String)Alias.get(db, aliasName, AliasFields.COMMAND) + " " + aliasName), originalAlias);
            }
        }
        catch (SQLException e) {
            App.logger.catching(e);
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
        }
        else {
            args = splitMsg[1].split(" ");
        }

        // TODO check rate limit for command and ignore if not enough time has passed

        try {
            if (Command.exists(db, cmdName)
                    && ((Integer)Command.get(db, cmdName, CommandFields.ENABLED) == 1)
                    && (userLevel.getValue() >= UserLevel.valueOf((String)Command.get(db, cmdName, CommandFields.EXEC_USER_LEVEL)).getValue())
                    && ((Integer)Command.get(db, cmdName, CommandFields.MIN_ARGS) <= args.length)) {

                String scriptPath = (String)Command.get(db, cmdName, CommandFields.SCRIPT);
                // Return script path
                if (scriptPath != null) {
                    return new ProcessedCommand(scriptPath, cmdName, true, args);
                }
                // Else non-script command
                // Check if command is debug
                else if ((int)Command.get(db, cmdName, CommandFields.DEBUG) == 0 || debug) {
                    String response = CommandResponseParser.parse(user, channel, (Integer) Command.get(db, cmdName, CommandFields.COUNT), args, (String) Command.get(db, cmdName, CommandFields.RESPONSE));
                    return new ProcessedCommand(response, cmdName, false, args);
                }
            }
        }
        catch (SQLException e) {
            App.logger.catching(e);
        }

        return new ProcessedCommand("", "", false, args);
    }
}
