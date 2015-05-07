package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FSCommandLoader {
    public static void LoadCommands() {
        // Get commands from files
        ArrayList<LoadedCommand> loadedCommands = getCommands(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.TO_LOAD);

        // Load commands
        File[] files = new File(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                DatabaseWrapper db = APIDatabase.getChannelMainDatabase(file.getName());
                // Load all-channels commands
                for (LoadedCommand command : loadedCommands) {
                    Command.addCommandFromLoadedCommand(db, command);
                }
                // Load its own commands
                ArrayList<LoadedCommand> channelCommands = getCommands(file.getPath() + File.separator + FSUtil.DirNames.TO_LOAD);
                for (LoadedCommand command : channelCommands) {
                    Command.addCommandFromLoadedCommand(db, command);
                }
            }
        }
    }

    public static void LoadAliases() {
        // Get aliases from files
        ArrayList<LoadedAlias> loadedAliases = getAliases(FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.TO_LOAD);

        // Load aliases
        File[] files = new File(FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                DatabaseWrapper db = APIDatabase.getChannelMainDatabase(file.getName());
                // Load all-channels aliases
                for (LoadedAlias alias : loadedAliases) {
                    Alias.addAliasFromLoadedAlias(db, alias);
                }
                // Load its own aliases
                ArrayList<LoadedAlias> channelAliases = getAliases(file.getPath() + File.separator + FSUtil.DirNames.TO_LOAD);
                for (LoadedAlias alias : channelAliases) {
                    Alias.addAliasFromLoadedAlias(db, alias);
                }
            }
        }
    }

    public static void LoadLoadedCommands(LoadingSet set) {
        File[] files = new File(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                try {
                    LoadLoadedCommands(file.getName(), set);
                } catch (IOException e) {
                    App.logger.catching(e);
                }
            }
        }
    }

    public static void LoadLoadedCommands(String channel, LoadingSet set) throws IOException {
        if (set == LoadingSet.BOTH) {
            LoadLoadedCommands(channel, LoadingSet.ALL_CHANNELS);
            LoadLoadedCommands(channel, LoadingSet.CHANNEL);
            return;
        }

        String channelPath = FSUtil.commandsDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel;
        if (!new File(channelPath).isDirectory()) {
            throw new IOException("Channel '" + channel + "' is not set up.");
        }

        DatabaseWrapper db = APIDatabase.getChannelMainDatabase(channel);
        String loadPath;
        if (set == LoadingSet.ALL_CHANNELS) {
            loadPath = FSUtil.commandsDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.LOADED;
        } else {
            loadPath = channelPath + File.separator + FSUtil.DirNames.LOADED;
        }

        ArrayList<LoadedCommand> loadedCommands = getCommands(loadPath);
        for (LoadedCommand command : loadedCommands) {
            Command.addCommandFromLoadedCommand(db, command);
        }
    }

    public static void LoadLoadedAliases(LoadingSet set) {
        File[] files = new File(FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                try {
                    LoadLoadedAliases(file.getName(), set);
                } catch (IOException e) {
                    App.logger.catching(e);
                }
            }
        }
    }

    public static void LoadLoadedAliases(String channel, LoadingSet set) throws IOException {
        if (set == LoadingSet.BOTH) {
            LoadLoadedAliases(channel, LoadingSet.ALL_CHANNELS);
            LoadLoadedAliases(channel, LoadingSet.CHANNEL);
            return;
        }

        String channelPath = FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.CHANNELS + File.separator + channel;
        if (!new File(channelPath).isDirectory()) {
            throw new IOException("Channel '" + channel + "' is not set up.");
        }

        DatabaseWrapper db = APIDatabase.getChannelMainDatabase(channel);
        String loadPath;
        if (set == LoadingSet.ALL_CHANNELS) {
            loadPath = FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.LOADED;
        } else {
            loadPath = channelPath + File.separator + FSUtil.DirNames.LOADED;
        }

        ArrayList<LoadedAlias> loadedAliases = getAliases(loadPath);
        for (LoadedAlias alias : loadedAliases) {
            Alias.addAliasFromLoadedAlias(db, alias);
        }
    }


    public static void LoadBotCommands() {
        // Get commands from files
        ArrayList<LoadedCommand> loadedCommands = getCommands(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DirNames.TO_LOAD);

        DatabaseWrapper db = APIDatabase.getBotDatabase();
        // Load all-channels commands
        for (LoadedCommand command : loadedCommands) {
            Command.addCommandFromLoadedCommand(db, command);
        }
    }

    public static void LoadBotAliases() {
        // Get aliases from files
        ArrayList<LoadedAlias> loadedAliases = getAliases(FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.TO_LOAD);

        DatabaseWrapper db = APIDatabase.getBotDatabase();
        // Load all-channels aliases
        for (LoadedAlias alias : loadedAliases) {
            Alias.addAliasFromLoadedAlias(db, alias);
        }
    }

    public static void LoadLoadedBotCommands() {
        // Get commands from files
        ArrayList<LoadedCommand> loadedCommands = getCommands(FSUtil.commandsDir() + File.separator + FSUtil.DirNames.BOT_CHANNEL + File.separator + FSUtil.DirNames.LOADED);

        DatabaseWrapper db = APIDatabase.getBotDatabase();
        // Load all-channels commands
        for (LoadedCommand command : loadedCommands) {
            Command.addCommandFromLoadedCommand(db, command);
        }
    }

    public static void LoadLoadedBotAliases() {
        // Get aliases from files
        ArrayList<LoadedAlias> loadedAliases = getAliases(FSUtil.aliasesDir() + File.separator + FSUtil.DirNames.ALL_CHANNELS + File.separator + FSUtil.DirNames.LOADED);

        DatabaseWrapper db = APIDatabase.getBotDatabase();
        // Load all-channels aliases
        for (LoadedAlias alias : loadedAliases) {
            Alias.addAliasFromLoadedAlias(db, alias);
        }
    }

    private static ArrayList<LoadedCommand> getCommands(String path) {
        // Get commands from files
        ArrayList<LoadedCommand> loadedCommands = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        for (File command : files) {
            if (command.isFile()) {
                LoadedCommand loadedCommand = JsonHandler.readValue(command.getPath(), LoadedCommand.class);
                try {
                    loadedCommand = CommandValidator.validateCommand(loadedCommand);
                    loadedCommands.add(loadedCommand);
                    handleSuccessfulLoad(command);
                } catch (InvalidCommandException e) {
                    handleFailedLoad(command);
                }
            }
        }
        return loadedCommands;
    }

    private static ArrayList<LoadedAlias> getAliases(String path) {
        // Get aliases from files
        ArrayList<LoadedAlias> loadedAliases = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        for (File alias : files) {
            if (alias.isFile()) {
                LoadedAlias loadedAlias = JsonHandler.readValue(alias.getPath(), LoadedAlias.class);
                try {
                    loadedAlias = CommandValidator.validateAlias(loadedAlias);
                    loadedAliases.add(loadedAlias);
                    handleSuccessfulLoad(alias);
                } catch (InvalidAliasException e) {
                    handleFailedLoad(alias);
                }
            }
        }
        return loadedAliases;
    }

    private static void handleFailedLoad(File file) {
        String failedPath = new File(new File(file.getParent()).getParent()) + File.separator + FSUtil.DirNames.FAILED + File.separator + file.getName();
        App.logger.info("Unable to load:\t" + file.getPath());
        if (!file.renameTo(new File(failedPath))) {
            App.logger.error("Failed to move file to:\t" + failedPath);
        }
    }

    private static void handleSuccessfulLoad(File file) {
        String successPath = new File(new File(file.getParent()).getParent()) + File.separator + FSUtil.DirNames.LOADED + File.separator + file.getName();
        App.logger.info("Successfully loaded:\t" + file.getPath());
        if (!move(file, new File(successPath))) {
            App.logger.error("Failed to move file to:\t" + successPath);
        }
    }

    private static boolean move(File source, File dest) {
        // Check if they're the same
        if (source.equals(dest)) {
            return true;
        }
        // Make sure overwrites
        if (dest.exists() && !dest.delete()) {
            App.logger.error("Failed to delete file '" + dest.getPath() + "' when trying to replace it with file '" + source.getPath() + "'");
        }
        return source.renameTo(dest);
    }
}
