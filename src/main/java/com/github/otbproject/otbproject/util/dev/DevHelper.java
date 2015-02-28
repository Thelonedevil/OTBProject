package com.github.otbproject.otbproject.util.dev;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.CommandLoader;
import com.github.otbproject.otbproject.commands.loader.FSCommandLoader;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.CommandFileGenerator;
import com.github.otbproject.otbproject.util.ConfigFileGenerator;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.io.IOException;

public class DevHelper {
    public static void run(String[] args) {
        doSetup();
        generateConfigFiles();
        generateCommandFiles();
        //stopProgramExecution();
    }

    private static void doSetup() {
        try {
            Setup.setup();
            Setup.setupChannel("the_lone_devil");
        } catch (IOException e) {
            App.logger.catching(e);
        }
    }

    private static void loadAliases() {
        FSCommandLoader.LoadAliases();
        DatabaseWrapper db = DatabaseHelper.getChannelDatabase("the_lone_devil");
        App.logger.info(Alias.getAliases(db));
    }

    private static void loadCommands() {
        FSCommandLoader.LoadCommands();
        DatabaseWrapper db = DatabaseHelper.getChannelDatabase("the_lone_devil");
        App.logger.info(Command.getCommands(db));
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

    private static void testCommandLoading() {
        LoadedCommand command = JsonHandler.readValue(FSUtil.defaultsDir() + File.separator + "example-command.json", LoadedCommand.class);
        DatabaseWrapper db = DatabaseHelper.getChannelDatabase("the_lone_devil");
        CommandLoader.addCommandFromLoadedCommand(db, command);

        App.logger.info("Command was successfully loaded:");
        App.logger.info(Command.exists(db, command.getName()));

        Command.remove(db, command.getName());
        App.logger.info("Command still exists:");
        App.logger.info(Command.exists(db, command.getName()));
    }

    private static void testMissingCommandField() {
        LoadedCommand command = JsonHandler.readValue(FSUtil.defaultsDir() + File.separator + "bad-command.json", LoadedCommand.class);
        if (command == null) {
            App.logger.info("Command read in as null.");
            return;
        }
        App.logger.info("Command object loaded.");

        if (command.getName() == null) {
            App.logger.info("Missing field: 'name'");
        } else {
            App.logger.info("Name is: " + command.getName());
        }

        App.logger.info("MinArgs: " + command.getMinArgs());
    }

    private static void testMissingChannelConfigField() {
        ChannelConfig config = JsonHandler.readValue(FSUtil.defaultsDir() + File.separator + "bad-channel-config.json", ChannelConfig.class);
        if (config == null) {
            App.logger.info("ChannelConfig read in as null.");
            return;
        }
        App.logger.info("ChannelConfig object loaded.");

        if (config.getCommandCooldown() == null) {
            App.logger.info("Missing field: 'commandCooldown'");
        } else {
            App.logger.info("Command cooldown is: " + config.getCommandCooldown());
        }
    }

    private static void stopProgramExecution() {
        throw new RuntimeException("Preventing connection to Twitch.");
    }
}
