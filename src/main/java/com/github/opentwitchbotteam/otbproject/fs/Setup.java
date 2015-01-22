package com.github.opentwitchbotteam.otbproject.fs;

import java.io.File;
import java.io.IOException;

public class Setup {
    public static void setup() throws IOException {
        // Commands Directory
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.ALL_CHANNELS_DIR_NAME + File.separator + FSUtil.LOADED_DIR_NAME);
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.ALL_CHANNELS_DIR_NAME + File.separator + FSUtil.TO_LOAD_DIR_NAME);
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.BOT_CHANNEL_DIR_NAME + File.separator + FSUtil.LOADED_DIR_NAME);
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.BOT_CHANNEL_DIR_NAME + File.separator + FSUtil.TO_LOAD_DIR_NAME);
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME);

        // Config Directory
        createDirs(FSUtil.CONFIG_DIR);

        // Data Directory
        createDirs(FSUtil.DATA_DIR + File.separator + FSUtil.BOT_CHANNEL_DIR_NAME);
        createDirs(FSUtil.DATA_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME);

        // Scripts Directory
        createDirs(FSUtil.SCRIPT_DIR);

        // TODO create config stuff
    }

    public static void setupChannel(String channel) throws IOException {
        if ((! new File(FSUtil.COMMANDS_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME).exists())
                || (! new File(FSUtil.DATA_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME).exists())) {
            setup(); // Because presumably it's not set up properly
        }

        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME + File.separator + channel + File.separator + FSUtil.LOADED_DIR_NAME);
        createDirs(FSUtil.COMMANDS_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME + File.separator + channel + File.separator + FSUtil.TO_LOAD_DIR_NAME);
        createDirs(FSUtil.DATA_DIR + File.separator + FSUtil.CHANNELS_DIR_NAME + File.separator + channel);

        // TODO create config file
    }

    private static void createDirs(String path) throws IOException {
        if (! new File(path).mkdirs()) {
            throw new IOException(FSUtil.ERROR_MSG + path);
        }
    }
}
