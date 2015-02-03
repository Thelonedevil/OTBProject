package com.github.opentwitchbotteam.otbproject.fs;

import java.io.File;

public class FSUtil {
    private static final String BASE_DIR_NAME = ".opentwitchbot";

    private static final String COMMANDS_DIR_NAME = "commands";
    private static final String CONFIG_DIR_NAME = "config";
    private static final String DATA_DIR_NAME = "data";
    private static final String SCRIPT_DIR_NAME = "scripts";


    public static final String ALL_CHANNELS_DIR_NAME = "all-channels";
    public static final String BOT_CHANNEL_DIR_NAME = "bot-channel";
    public static final String CHANNELS_DIR_NAME = "channels";

    public static final String LOADED_DIR_NAME = "loaded";
    public static final String TO_LOAD_DIR_NAME = "to-load";


    public static final String BASE_DIR = System.getProperty("user.home") + File.separator + BASE_DIR_NAME;
    public static final String COMMANDS_DIR = BASE_DIR + File.separator + COMMANDS_DIR_NAME;
    public static final String CONFIG_DIR = BASE_DIR + File.separator + CONFIG_DIR_NAME;
    public static final String DATA_DIR = BASE_DIR + File.separator + DATA_DIR_NAME;
    public static final String SCRIPT_DIR = BASE_DIR + File.separator + SCRIPT_DIR_NAME;

    public static final String DATABASE_NAMES_MAIN = "main.db";

    public static final String ERROR_MSG = "Failed to create directory: ";
}
