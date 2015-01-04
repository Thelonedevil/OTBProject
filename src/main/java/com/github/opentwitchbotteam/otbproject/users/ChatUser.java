package com.github.opentwitchbotteam.otbproject.users;

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;

import java.sql.SQLException;
import java.util.HashMap;

public class ChatUser {
    /**
     *
     * @param userNick
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> getDetails(DatabaseWrapper db, String userNick) throws SQLException {
        return db.getRow(UserFields.TABLE_NAME, userNick, UserFields.NICK);
    }

    public static Object get(DatabaseWrapper db, String userNick, String fieldToGet) throws SQLException {
        return db.getValue(UserFields.TABLE_NAME, userNick, UserFields.NICK, fieldToGet);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void update(DatabaseWrapper db, HashMap map) throws SQLException {
        db.updateRow(UserFields.TABLE_NAME, (String) map.get(UserFields.NICK), UserFields.NICK, map);
    }

    public static void update(DatabaseWrapper db, String userNick, String fieldName, Object fieldValue) throws SQLException {
        HashMap<String,Object> map = getDetails(db, userNick);
        map.replace(fieldName,fieldValue);
        update(db, map);
    }

    public static boolean exists(DatabaseWrapper db, String userNick) throws SQLException {
        return db.exists(UserFields.TABLE_NAME, userNick, UserFields.NICK);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void add(DatabaseWrapper db, HashMap map) throws SQLException {
        db.insertRow(UserFields.TABLE_NAME, (String) map.get(UserFields.NICK), UserFields.NICK, map);
    }

    public static void remove(DatabaseWrapper db, String userNick) throws SQLException {
        db.removeRow(UserFields.TABLE_NAME, userNick, UserFields.NICK);
    }
}
