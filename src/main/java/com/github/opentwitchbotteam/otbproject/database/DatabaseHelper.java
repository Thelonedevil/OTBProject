package com.github.opentwitchbotteam.otbproject.database;

import com.github.opentwitchbotteam.otbproject.commands.AliasFields;
import com.github.opentwitchbotteam.otbproject.commands.CommandFields;
import com.github.opentwitchbotteam.otbproject.users.UserFields;

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
}
