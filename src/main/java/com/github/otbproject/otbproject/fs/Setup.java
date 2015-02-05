package com.github.otbproject.otbproject.fs;

import java.io.File;
import java.io.IOException;

public class Setup {
    public static void setup() throws IOException {
        // Commands Directory
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.LOADED);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.TO_LOAD);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.FAILED);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DirNames.LOADED);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DirNames.TO_LOAD);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DirNames.FAILED);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS);

        // Config Directory
        createDirs(FSUtil.configDir());
        // TODO create config stuff

        // Data Directory
        createDirs(FSUtil.dataDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL);
        createDirs(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS);

        // Logs Directory
        createDirs(FSUtil.logsDir());

        // Scripts Directory
        createDirs(FSUtil.scriptDir());
    }

    public static void setupChannel(String channel) throws IOException {
        if ((! new File(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS).exists())
                || (! new File(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS).exists())) {
            setup(); // Because presumably it's not set up properly
        }

        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DirNames.LOADED);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DirNames.TO_LOAD);
        createDirs(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel + File.separator + FSUtil.DirNames.FAILED);
        createDirs(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel);

        // TODO create config file
    }

    private static void createDirs(String path) throws IOException {
        if (! new File(path).mkdirs()) {
            throw new IOException(FSUtil.ERROR_MSG + path);
        }
    }
}
