package com.github.otbproject.otbproject.util.dev;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.CommandLoader;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.CommandFileGenerator;
import com.github.otbproject.otbproject.util.ConfigFileGenerator;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DevHelper {
    public static void run(String[] args) {
        try {
            Setup.setup();
            Setup.setupChannel("the_lone_devil");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //generateConfigFiles();
        //generateCommandFiles();

        // Test command loading
        LoadedCommand command = JsonHandler.readValue(FSUtil.defaultsDir() + File.separator + "example-command.json", LoadedCommand.class);
        DatabaseWrapper db = DatabaseHelper.getChannelDatabase("the_lone_devil");
        CommandLoader.addCommandFromLoadedCommand(db, command);
        App.logger.info("Command was successfully loaded:");
        try {
            App.logger.info(Command.exists(db, "!example-command-name"));
        } catch (SQLException e) {
            App.logger.catching(e);
        }
    }

    private static void generateConfigFiles() {
        ConfigFileGenerator.generateAccountConfig();
        ConfigFileGenerator.generateBotConfig();
        ConfigFileGenerator.generateChannelConfig();
        ConfigFileGenerator.generateGeneralConfig();
    }

    private static void generateCommandFiles() {
        CommandFileGenerator.generateCommandFile();
        CommandFileGenerator.generateAliasFile();
    }
}
