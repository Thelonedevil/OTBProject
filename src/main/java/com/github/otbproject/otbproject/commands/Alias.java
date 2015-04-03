package com.github.otbproject.otbproject.commands;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Alias {

    public static LoadedAlias get(DatabaseWrapper db, String aliasName) {
        if (db.exists(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME)) {
            LoadedAlias loadedAlias = new LoadedAlias();
            ResultSet rs = db.getRecord(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
            try {
                loadedAlias.setName(rs.getString(AliasFields.NAME));
                loadedAlias.setCommand(rs.getString(AliasFields.COMMAND));
                loadedAlias.setModifyingUserLevel(UserLevel.valueOf(rs.getString(AliasFields.MODIFYING_UL)));
                loadedAlias.setEnabled(Boolean.valueOf(rs.getString(AliasFields.ENABLED)));
                return loadedAlias;
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
        return null;
    }

    public static ArrayList<String> getAliases(DatabaseWrapper db) {
        ArrayList<Object> objectArrayList =  db.getRecordsList(AliasFields.TABLE_NAME, AliasFields.NAME);
        if (objectArrayList == null) {
            return null;
        }
        ArrayList<String> aliasesList = new ArrayList<>();
        try {
            aliasesList.addAll(objectArrayList.stream().map(key -> (String) key).collect(Collectors.toList()));
            return aliasesList;
        } catch (ClassCastException e) {
            App.logger.catching(e);
            return null;
        }
    }

    public static boolean update(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.updateRecord(AliasFields.TABLE_NAME, map.get(AliasFields.NAME), AliasFields.NAME, map);
    }

    public static boolean exists(DatabaseWrapper db, String aliasName) {
        return db.exists(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    public static boolean add(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.insertRecord(AliasFields.TABLE_NAME, map);
    }

    public static boolean remove(DatabaseWrapper db, String aliasName) {
        return db.removeRecord(AliasFields.TABLE_NAME, aliasName, AliasFields.NAME);
    }

    public static boolean addAliasFromLoadedAlias(DatabaseWrapper db, LoadedAlias loadedAlias) {
        HashMap<String, Object> map = new HashMap<>();

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
