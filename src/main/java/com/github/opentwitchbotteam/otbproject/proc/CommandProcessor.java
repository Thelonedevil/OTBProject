package com.github.opentwitchbotteam.otbproject.proc;

import com.github.opentwitchbotteam.otbproject.commands.Alias;
import com.github.opentwitchbotteam.otbproject.commands.AliasFields;
import com.github.opentwitchbotteam.otbproject.commands.Command;
import com.github.opentwitchbotteam.otbproject.commands.CommandFields;
import com.github.opentwitchbotteam.otbproject.commands.parser.CommandResponseParser;
import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;
import com.github.opentwitchbotteam.otbproject.users.UserLevel;

import java.sql.SQLException;

public class CommandProcessor {
    public static void processCommand(DatabaseWrapper db, String message, String execChannel, String targetChannel, String user, boolean subscriber) {
        String command = checkAlias(db, message, "");


    }

    private static String checkAlias(DatabaseWrapper db, String message, String originalAlias) {
        String[] splitMsg = message.split(" ", 2);
        if (originalAlias.equals("")) {
            originalAlias = splitMsg[0];
        }

        try {
            if (Alias.exists(db, splitMsg[0]) && ((Integer)Alias.get(db, splitMsg[0], AliasFields.ENABLED) == 1)) {
                if (splitMsg.length == 1) {
                    return checkAlias(db, (String)Alias.get(db, splitMsg[0], AliasFields.COMMAND), originalAlias);
                }
                return checkAlias(db, ((String)Alias.get(db, splitMsg[0], AliasFields.COMMAND) + " " + splitMsg[1]), originalAlias);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return message;
    }

    private static String checkCommand(DatabaseWrapper db, String message, String execChannel, String targetChannel, String user, boolean subscriber) {
        UserLevel userLevel = Util.getUserLevel(db, targetChannel, user, subscriber);
        return checkCommand(db, message, execChannel, targetChannel, user, userLevel);
    }

    // Returns an empty string if script command
    private static String checkCommand(DatabaseWrapper db, String message, String execChannel, String targetChannel, String user, UserLevel userLevel) {
        String[] splitMsg = message.split(" ", 2);

        try {
            if (Command.exists(db, splitMsg[0]) && ((Integer)Command.get(db, splitMsg[0], CommandFields.ENABLED) == 1) && (userLevel.getValue() >= UserLevel.valueOf((String)Command.get(db, splitMsg[0], CommandFields.EXEC_USER_LEVEL)).getValue())) {
                String scriptPath = (String)Command.get(db, splitMsg[0], CommandFields.SCRIPT);
                if (scriptPath == null) {
                    if (splitMsg.length == 1) {
                        return CommandResponseParser.parse(user, (Integer)Command.get(db, splitMsg[0], CommandFields.COUNT), new String[0], (String)Command.get(db, splitMsg[0], CommandFields.RESPONSE));
                    }
                    return CommandResponseParser.parse(user, (Integer)Command.get(db, splitMsg[0], CommandFields.COUNT), splitMsg[1].split(" "), (String)Command.get(db, splitMsg[0], CommandFields.RESPONSE));
                }
                // Else run script command
                if (splitMsg.length == 1) {
                    ScriptProcessor.processScript(scriptPath, db, new String[0], execChannel, targetChannel, user, userLevel);
                }
                else {
                    ScriptProcessor.processScript(scriptPath, db, splitMsg[1].split(" "), execChannel, targetChannel, user, userLevel);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }
}
