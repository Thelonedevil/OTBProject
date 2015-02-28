package com.github.otbproject.otbproject.commands;

import java.util.HashMap;
import java.util.HashSet;

public class AliasFields {
    public static final String NAME = "name";
    public static final String COMMAND = "command";
    public static final String MODIFYING_UL = "modifyingUL";
    public static final String ENABLED = "enabled";

    public static final String TABLE_NAME = "tblAliases";

    public static HashSet<String> getTableHashSet(){
        HashSet<String> commandFields = new HashSet<>();
        commandFields.add(NAME);
        commandFields.add(COMMAND);
        commandFields.add(MODIFYING_UL);

        commandFields.add(ENABLED); // TODO possibly remove
        return commandFields;
    }
}
