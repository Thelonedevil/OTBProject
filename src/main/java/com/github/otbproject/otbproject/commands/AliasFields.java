package com.github.otbproject.otbproject.commands;

import com.github.otbproject.otbproject.database.DataTypes;
import com.github.otbproject.otbproject.database.TableFields;

import java.util.HashMap;

public class AliasFields {
    public static final String NAME = "name";
    public static final String COMMAND = "command";
    public static final String MODIFYING_UL = "modifyingUL";
    public static final String ENABLED = "enabled";

    public static final String TABLE_NAME = "tblAliases";
    public static final String PRIMARY_KEY = NAME;

    public static TableFields getTableFields() {
        HashMap<String,String> aliasFields = new HashMap<>();
        aliasFields.put(NAME, DataTypes.STRING);
        aliasFields.put(COMMAND, DataTypes.STRING);
        aliasFields.put(MODIFYING_UL, DataTypes.STRING);
        aliasFields.put(ENABLED, DataTypes.STRING);
        return new TableFields(aliasFields, PRIMARY_KEY);
    }
}
