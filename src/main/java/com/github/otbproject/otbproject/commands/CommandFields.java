package com.github.otbproject.otbproject.commands;


import java.util.HashMap;
import java.util.HashSet;

public class CommandFields {
    public static final String NAME = "name";
    public static final String RESPONSE = "response";
    public static final String EXEC_USER_LEVEL = "execUserLevel";
    public static final String MIN_ARGS = "minArgs";
    public static final String COUNT = "count";
    public static final String NAME_MODIFYING_UL = "nameModifyingUL";
    public static final String RESPONSE_MODIFYING_UL = "responseModifyingUL";
    public static final String USER_LEVEL_MODIFYING_UL = "userLevelModifyingUL";
    public static final String SCRIPT = "script";
    public static final String ENABLED = "enabled";
    public static final String DEBUG = "debug"; // If true, only sends if channel set for debug

    public static final String TABLE_NAME = "tblCommands";

    public static HashSet<String> getTableHashSet()
    {
        HashSet<String> commandFields = new HashSet<>();
        commandFields.add(NAME);
        commandFields.add(RESPONSE);
        commandFields.add(EXEC_USER_LEVEL);
        commandFields.add(MIN_ARGS);
        commandFields.add(COUNT);
        commandFields.add(NAME_MODIFYING_UL);
        commandFields.add(RESPONSE_MODIFYING_UL);
        commandFields.add(USER_LEVEL_MODIFYING_UL);
        commandFields.add(SCRIPT);
        commandFields.add(ENABLED);
        commandFields.add(DEBUG);

        return commandFields;
    }
}
