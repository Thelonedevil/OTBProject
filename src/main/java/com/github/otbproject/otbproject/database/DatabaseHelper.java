package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.commands.AliasFields;
import com.github.otbproject.otbproject.commands.CommandFields;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.users.UserFields;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class DatabaseHelper {
    /**
     * @return a HashMap used to create all the tables by the DatabaseWrapper.
     * Tables are hard-coded into the method.
     */
    public static HashMap<String, HashSet<String>> getTablesHashMap() {
        HashMap<String, HashSet<String>> tables = new HashMap<>();
        tables.put(CommandFields.TABLE_NAME, CommandFields.getTableHashSet());
        tables.put(AliasFields.TABLE_NAME, AliasFields.getTableHashSet());
        tables.put(UserFields.TABLE_NAME, UserFields.getTableHashSet());

        return tables;
    }

    public static DatabaseWrapper getChannelDatabase(String channel) {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDataBase(path, getTablesHashMap());
    }

    public static DatabaseWrapper getBotDatabase() {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDataBase(path, getTablesHashMap());
    }
}
