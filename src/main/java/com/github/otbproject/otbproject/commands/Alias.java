package com.github.otbproject.otbproject.commands;

import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.sql.SQLException;
import java.util.HashMap;

public class Alias {
    /**
     *
     * @param aliasName
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> getDetails(DatabaseWrapper db, String aliasName) throws SQLException {
        return db.getRow(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    public static Object get(DatabaseWrapper db, String aliasName, String fieldToGet) throws SQLException {
        return db.getValue(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME, fieldToGet);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void update(DatabaseWrapper db, HashMap map) throws SQLException {
        db.updateRow(AliasFields.TABLE_NAME, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static void update(DatabaseWrapper db, String aliasName, String fieldName, Object fieldValue) throws SQLException {
        HashMap<String,Object> map = getDetails(db, aliasName);
        map.replace(fieldName,fieldValue);
        update(db, map);
    }

    public static boolean exists(DatabaseWrapper db, String aliasName) throws SQLException {
        return db.exists(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public static void add(DatabaseWrapper db, HashMap map) throws SQLException {
        db.insertRow(AliasFields.TABLE_NAME, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static void remove(DatabaseWrapper db, String aliasName) throws SQLException {
        db.removeRow(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }
}
