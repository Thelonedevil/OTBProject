package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.commands.AliasFields;
import com.github.otbproject.otbproject.commands.CommandFields;
import com.github.otbproject.otbproject.users.UserFields;

import java.util.HashMap;

public class DatabaseHelper {
    /**
     * @return a HashMap used to create all the tables by the DatabaseWrapper.
     * Tables are hard-coded into the method.
     */
    public static HashMap<String, TableFields> getTablesHashMap() {
        HashMap<String, TableFields> tables = new HashMap<>();
        tables.put(CommandFields.TABLE_NAME, CommandFields.getTableFields());
        tables.put(AliasFields.TABLE_NAME, AliasFields.getTableFields());
        tables.put(UserFields.TABLE_NAME, UserFields.getTableFields());
        return tables;
    }
}
