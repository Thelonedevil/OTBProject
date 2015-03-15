package com.github.otbproject.otbproject.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Alias {

    public static LoadedAlias get(DatabaseWrapper db, String aliasName) {
        LoadedAlias loadedAlias = new LoadedAlias();
        if (db.exists(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME)) {
            ResultSet rs = db.getRecord(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
            try {
                loadedAlias.setName(rs.getString(AliasFields.NAME));
                loadedAlias.setCommand(rs.getString(AliasFields.COMMAND));
                loadedAlias.setModifyingUserLevel(UserLevel.valueOf(rs.getString(AliasFields.MODIFYING_UL)));
                loadedAlias.setEnabled(Boolean.valueOf(rs.getString(AliasFields.ENABLED)));
            } catch (SQLException e) {
                App.logger.catching(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    App.logger.catching(e);
                }
            }
        }
        return loadedAlias;
    }

    public static ArrayList<String> getAliases(DatabaseWrapper db) {
        return db.getRecordsList(AliasFields.TABLE_NAME, AliasFields.NAME);
    }

    public static boolean update(DatabaseWrapper db, HashMap map) {
        return db.updateRecord(AliasFields.TABLE_NAME, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static boolean exists(DatabaseWrapper db, String aliasName) {
        return db.exists(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    public static boolean add(DatabaseWrapper db, HashMap map) {
        return db.insertRecord(AliasFields.TABLE_NAME, (String) map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static boolean remove(DatabaseWrapper db, String aliasName) {
        return db.removeRecord(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    public static boolean addAliasFromLoadedAlias(DatabaseWrapper db, LoadedAlias loadedAlias) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(AliasFields.NAME, loadedAlias.getName());
        map.put(AliasFields.COMMAND, loadedAlias.getCommand());
        map.put(AliasFields.MODIFYING_UL, loadedAlias.getModifyingUserLevel().name());
        map.put(AliasFields.ENABLED, String.valueOf(loadedAlias.isEnabled()));
        if (Alias.exists(db, loadedAlias.getName())) {
            return Alias.update(db, map);
        } else {
            return Alias.add(db, map);
        }
    }
}
