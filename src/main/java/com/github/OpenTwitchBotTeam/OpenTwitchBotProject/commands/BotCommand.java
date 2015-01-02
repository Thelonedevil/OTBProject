package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.commands;


import com.github.OpenTwitchBotTeam.OpenTwitchBotProject.database.DatabaseWrapper;
import com.github.OpenTwitchBotTeam.OpenTwitchBotProject.database.TableNames;

import java.sql.SQLException;
import java.util.HashMap;

public class BotCommand {

    public static final String RUN = "run";

    /**
     *
     * @param command
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> getDetails(DatabaseWrapper db, String command) throws SQLException {
        return db.getRow(TableNames.COMMANDS, command, CommandFields.NAME);
    }

    public static Object get(DatabaseWrapper db, String command, String fieldToGet) throws SQLException {
        return db.getValue(TableNames.COMMANDS, command, CommandFields.NAME, fieldToGet);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void update(DatabaseWrapper db, HashMap map) throws SQLException {
        db.updateRow(TableNames.COMMANDS, (String) map.get(CommandFields.NAME), CommandFields.NAME, map);
    }

    public static void update(DatabaseWrapper db, String command, String fieldName, Object fieldValue) throws SQLException {
        HashMap<String,Object> map = getDetails(db, command);
        map.replace(fieldName,fieldValue);
        update(db, map);
    }

    public static boolean exists(DatabaseWrapper db, String command) throws SQLException {
        return db.exists(TableNames.COMMANDS, command, CommandFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void add(DatabaseWrapper db, HashMap map) throws SQLException {
        db.insertRow(TableNames.COMMANDS, (String) map.get(CommandFields.NAME), CommandFields.NAME, map);
    }

    public static void remove(DatabaseWrapper db, String command) throws SQLException {
        db.removeRow(TableNames.COMMANDS, command, CommandFields.NAME);
    }

    public static void incrementCount(DatabaseWrapper db, String command) throws SQLException {
        update(db, command, CommandFields.COUNT, ((Integer) getDetails(db, command).get(CommandFields.COUNT) + 1));
    }

    public static void resetCount(DatabaseWrapper db, String command) throws SQLException{
        update(db, command, CommandFields.COUNT, 0);
    }
}
