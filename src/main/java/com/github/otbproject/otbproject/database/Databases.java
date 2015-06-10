package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;

public class Databases {
    public static DatabaseWrapper getChannelMainDatabase(String channel) {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDatabase(path, DatabaseHelper.getMainTablesHashMap());
    }

    public static SQLiteQuoteWrapper getChannelQuoteDatabase(String channel) {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DatabaseNames.QUOTES;
        return SQLiteQuoteWrapper.createDatabase(path, DatabaseHelper.getQuoteTablesHashMap());
    }

    public static DatabaseWrapper getBotDatabase() {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDatabase(path, DatabaseHelper.getMainTablesHashMap());
    }
}
