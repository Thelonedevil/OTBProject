package com.github.otbproject.otbproject.fs;

import com.github.otbproject.otbproject.util.dev.Flags;

import java.io.File;

public class FSUtil {
    private static final String BASE_DIR_NAME = ".otbproject";

    private static final String COMMANDS_DIR_NAME = "commands";
    private static final String CONFIG_DIR_NAME = "config";
    private static final String DATA_DIR_NAME = "data";
    private static final String DEFAULTS_DIR_NAME = "defaults";
    private static final String LOGS_DIR_NAME = "logs";
    private static final String SCRIPT_DIR_NAME = "scripts";

    public static class DirNames {
        public static final String ALL_CHANNELS = "all-channels";
        public static final String BOT_CHANNEL = "bot-channel";
        public static final String CHANNELS = "channels";

        public static final String LOADED = "loaded";
        public static final String TO_LOAD = "to-load";
        public static final String FAILED = "failed";
    }

    public static class DatabaseNames {
        public static final String MAIN = "main.db";
        public static final String QUOTES = "quotes.db";
    }

    public static final String BASE_DIR_DEFAULT = System.getProperty("user.home") + File.separator + BASE_DIR_NAME;
    public static final String ERROR_MSG = "Failed to create directory: ";

    private static String baseDir = BASE_DIR_DEFAULT;

    public static String getBaseDir() {
        return baseDir;
    }

    // Assumes path does not have a trailing slash
    public static void setBaseDirPath(String path) {
        baseDir = path + File.separator + BASE_DIR_NAME;
    }

    public static String commandsDir() {
        return baseDir + File.separator + COMMANDS_DIR_NAME;
    }

    public static String configDir() {
        return baseDir + File.separator + CONFIG_DIR_NAME;
    }

    public static String dataDir() {
        return baseDir + File.separator + DATA_DIR_NAME;
    }

    public static String defaultsDir() {
        return baseDir + File.separator + DEFAULTS_DIR_NAME;
    }

    public static String logsDir() {
        return baseDir + File.separator + LOGS_DIR_NAME;
    }

    public static String scriptDir() {
        // TODO remove for release
        if (Flags.DEV) {
            return System.getProperty("user.home") + File.separator + "script-dir-link";
        }
        return baseDir + File.separator + SCRIPT_DIR_NAME;
    }
}
