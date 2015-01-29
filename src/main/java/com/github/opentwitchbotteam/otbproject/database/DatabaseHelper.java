package com.github.opentwitchbotteam.otbproject.database;

import com.github.opentwitchbotteam.otbproject.commands.AliasFields;
import com.github.opentwitchbotteam.otbproject.commands.CommandFields;
import com.github.opentwitchbotteam.otbproject.fs.FSUtil;
import com.github.opentwitchbotteam.otbproject.users.UserFields;

import java.io.File;
import java.util.HashMap;

public class DatabaseHelper {
    /**
     *
     * @return a HashMap used to create all the tables by the DatabaseWrapper.
     * Tables are hard-coded into the method.
     */
    public static HashMap<String, HashMap<String, String>> getTablesHashMap() {
        HashMap<String, HashMap<String, String>> tables = new HashMap<String, HashMap<String, String>>();
        tables.put(CommandFields.TABLE_NAME, CommandFields.getTableHashMap());
        tables.put(AliasFields.TABLE_NAME, AliasFields.getTableHashMap());
        tables.put(UserFields.TABLE_NAME, UserFields.getTableHashMap());

        return tables;
    }

    public static DatabaseWrapper getChannelDatabase(String channel) {
        String path = FSUtil.DATA_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME + File.separator + channel + File.separator + FSUtil.DATABASE_NAMES_MAIN;
        return new DatabaseWrapper(path, getTablesHashMap());
    }

    public static DatabaseWrapper getBotDatabase() {
        String path = FSUtil.DATA_DIR + File.separator + FSUtil.BOT_CHANNEL_DIR_NAME + File.separator + FSUtil.DATABASE_NAMES_MAIN;
        return new DatabaseWrapper(path, getTablesHashMap());
    }
}
