package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;

public class APIDatabase {
    public static DatabaseWrapper getChannelMainDatabase(String channel) {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDatabase(path, DatabaseHelper.getTablesHashMap());
    }

    public static DatabaseWrapper getBotDatabase() {
        String path = FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DatabaseNames.MAIN;
        return DatabaseWrapper.createDatabase(path, DatabaseHelper.getTablesHashMap());
    }
}
