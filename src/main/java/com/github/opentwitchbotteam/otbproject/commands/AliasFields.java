package com.github.opentwitchbotteam.otbproject.commands;

import java.util.HashMap;

public class AliasFields {
    public static final String NAME = "name";
    public static final String COMMAND = "command";
    public static final String MODIFYING_UL = "modifyingUL";
    public static final String ENABLED = "enabled"; // TODO keep?

    public static final String TABLE_NAME = "tblAliases";

    public static HashMap<String, String> getTableHashMap()
    {
        HashMap<String, String> commandFields = new HashMap<String, String>();
        commandFields.put(NAME, "Text");
        commandFields.put(COMMAND, "Text");
        commandFields.put(MODIFYING_UL, "Text");

        commandFields.put(ENABLED, "Integer"); // TODO possibly remove

        return commandFields;
    }
}
