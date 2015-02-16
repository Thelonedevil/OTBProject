package com.github.otbproject.otbproject.commands;


import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Command {
    /**
     *
     * @param commandName
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> getDetails(DatabaseWrapper db, String commandName) throws SQLException {
        return db.getRow(CommandFields.TABLE_NAME, commandName, CommandFields.NAME);
    }

    public static Object get(DatabaseWrapper db, String commandName, String fieldToGet) throws SQLException {
        return db.getValue(CommandFields.TABLE_NAME, commandName, CommandFields.NAME, fieldToGet);
    }

    public static HashMap<String,HashMap<String,Object>> getCommandsWithInfo(DatabaseWrapper db) throws SQLException {
        return db.getRecords(AliasFields.TABLE_NAME,AliasFields.NAME);
    }

    public static ArrayList<String> getCommands(DatabaseWrapper db) throws SQLException {
        return db.getRecordsList(CommandFields.TABLE_NAME,CommandFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void update(DatabaseWrapper db, HashMap map) throws SQLException {
        db.updateRow(CommandFields.TABLE_NAME, (String) map.get(CommandFields.NAME), CommandFields.NAME, map);
    }

    public static void update(DatabaseWrapper db, String commandName, String fieldName, Object fieldValue) throws SQLException {
        HashMap<String,Object> map = getDetails(db, commandName);
        map.replace(fieldName,fieldValue);
        update(db, map);
    }

    public static boolean exists(DatabaseWrapper db, String commandName) throws SQLException {
        return db.exists(CommandFields.TABLE_NAME, commandName, CommandFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void add(DatabaseWrapper db, HashMap map) throws SQLException {
        db.insertRow(CommandFields.TABLE_NAME, (String) map.get(CommandFields.NAME), CommandFields.NAME, map);
    }

    public static void remove(DatabaseWrapper db, String commandName) throws SQLException {
        db.removeRow(CommandFields.TABLE_NAME, commandName, CommandFields.NAME);
    }

    public static void incrementCount(DatabaseWrapper db, String commandName) throws SQLException {
        update(db, commandName, CommandFields.COUNT, ((Integer) getDetails(db, commandName).get(CommandFields.COUNT) + 1));
    }

    public static void resetCount(DatabaseWrapper db, String commandName) throws SQLException{
        update(db, commandName, CommandFields.COUNT, 0);
    }
}
