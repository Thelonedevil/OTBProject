package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.commands;

import com.github.OpenTwitchBotTeam.OpenTwitchBotProject.database.DatabaseWrapper;
import com.github.OpenTwitchBotTeam.OpenTwitchBotProject.database.TableNames;

import java.sql.SQLException;
import java.util.HashMap;

public class Alias {
    /**
     *
     * @param command
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> getDetails(DatabaseWrapper db, String command) throws SQLException {
        return db.getRow(TableNames.ALIASES, command, AliasFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void update(DatabaseWrapper db, HashMap map) throws SQLException {
        db.updateRow(TableNames.ALIASES, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static void update(DatabaseWrapper db, String command, String fieldName, Object fieldValue) throws SQLException {
        HashMap<String,Object> map = getDetails(db, command);
        map.replace(fieldName,fieldValue);
        update(db, map);
    }

    public static boolean exists(DatabaseWrapper db, String command) throws SQLException {
        return db.exists(TableNames.ALIASES, command, AliasFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void add(DatabaseWrapper db, HashMap map) throws SQLException {
        db.insertRow(TableNames.ALIASES, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static void remove(DatabaseWrapper db, String command) throws SQLException {
        db.removeRow(TableNames.ALIASES, command, AliasFields.NAME);
    }
}
