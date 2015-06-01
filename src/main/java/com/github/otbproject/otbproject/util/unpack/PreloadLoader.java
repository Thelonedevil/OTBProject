package com.github.otbproject.otbproject.util.unpack;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.*;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.filters.*;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.fs.groups.Load;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreloadLoader {
    public static void loadDirectory(Base base, Chan chan, String channelName, LoadStrategy strategy) {
        List<PreloadPair> list = loadDirectoryContents(base, chan, channelName, strategy);
        if (list == null) {
            return;
        }

        switch (chan) {
            case ALL:
                loadForAllChannels(list, channelName, base, strategy);
                break;
            case SPECIFIC:
                loadForChannel(list, channelName, base, strategy);
                break;
            case BOT:
                loadForBotChannel(list, base, strategy);
                break;
        }
    }

    private static void loadForAllChannels(List<PreloadPair> list, String channelName, Base base, LoadStrategy strategy) {
        if (channelName != null) {
            loadForChannel(list, channelName, base, strategy);
            return;
        }

        File[] files = new File(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            App.logger.error("Unable to get list of channels");
            return;
        }

        App.logger.info("Loading objects of type '" + base.toString() + "' for all channels");
        Stream.of(files).filter(File::isDirectory)
                .forEach(file -> loadForChannel(list, file.getName(), base, strategy));
        App.logger.info("Finished loading objects of type '" + base.toString() + "' for all channels");
    }

    private static void loadForChannel(List<PreloadPair> list, String channel, Base base, LoadStrategy strategy) {
        App.logger.info("Loading objects of type '" + base.toString() + "' for channel: " + channel);
        DatabaseWrapper db = APIDatabase.getChannelMainDatabase(channel);
        if (db == null) {
            App.logger.error("Unable to get database for channel: " + channel);
            return;
        }
        loadFromList(list, db, base, strategy);
        App.logger.info("Finished loading objects of type '" + base.toString() + "' for channel: " + channel);
    }

    private static void loadForBotChannel(List<PreloadPair> list, Base base, LoadStrategy strategy) {
        App.logger.info("Loading objects of type '" + base.toString() + "' for bot channel");
        DatabaseWrapper db = APIDatabase.getBotDatabase();
        if (db == null) {
            App.logger.error("Unable to get bot database");
            return;
        }
        loadFromList(list, db, base, strategy);
        App.logger.info("Finished loading objects of type '" + base.toString() + "' for bot channel");
    }

    private static void loadFromList(List<PreloadPair> list, DatabaseWrapper db, Base base, LoadStrategy strategy) {
        list.forEach(preloadPair -> loadObjectUsingStrategy(db, preloadPair.tNew, preloadPair.tOld, base, strategy));
    }

    private static <T> void loadObjectUsingStrategy(DatabaseWrapper db, T tNew, T tOld, Base base, LoadStrategy strategy) {
        try {
            switch (base) {
                case ALIAS:
                    LoadedAlias alias = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateAliasHybrid(db, (LoadedAlias) tNew, (LoadedAlias) tOld) : (LoadedAlias) tNew;
                    if (alias == null) {
                        break;
                    }
                    Alias.addAliasFromLoadedAlias(db, alias);
                    break;
                case CMD:
                    LoadedCommand command = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateCommandHybrid(db, (LoadedCommand) tNew, (LoadedCommand) tOld) : (LoadedCommand) tNew;
                    if (command == null) {
                        break;
                    }
                    Command.addCommandFromLoadedCommand(db, command);
                    break;
                case FILTER:
                    BasicFilter filter = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateFilterHybrid(db, (BasicFilter) tNew, (BasicFilter) tOld) : (BasicFilter) tNew;
                    if (filter == null) {
                        break;
                    }
                    Filters.addFilterFromObj(db, filter);
                    break;
                case FILTER_GRP:
                    FilterGroup group = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateFilterGroupHybrid(db, (FilterGroup) tNew, (FilterGroup) tOld) : (FilterGroup) tNew;
                    if (group == null) {
                        break;
                    }
                    FilterGroups.addFilterGroupFromObj(db, group);
                    break;
                default:
                    App.logger.warn("Unable to load object into database based on base: " + base.toString());
            }
        } catch (ClassCastException e) {
            App.logger.catching(e);
        }
    }

    private static List<PreloadPair> loadDirectoryContents(Base base, Chan chan, String channelName, LoadStrategy strategy) {
        File dir = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.TO).asFile();
        File[] files = dir.listFiles();
        if (files == null) {
            App.logger.error("Unable to get list of files for directory: " + dir.toString());
            return null;
        }

        final Class tClass = getClassFromBase(base);
        if (tClass == null) {
            App.logger.warn("Unable to determine class to unpack as for base: " + base.toString());
            return null;
        }

        List<PreloadPair> list = new ArrayList<>();
        Stream.of(files)
                .forEach(file -> {
                    String name = file.getName();
                    String pathOld = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.ED).create() + File.separator + name;
                    String pathNew;
                    if (strategy == LoadStrategy.FROM_LOADED) {
                        pathNew = pathOld;
                    } else {
                        pathNew = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.TO).create() + File.separator + name;
                    }
                    String pathFail = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.FAIL).create() + File.separator + name;
                    list.add(loadFromFile(pathNew, pathOld, pathFail, tClass, base, strategy));
                });
                //.collect(Collectors.toList());
        return list;
    }

    private static <T> PreloadPair<T> loadFromFile(String pathNew, String pathOld, String pathFail, Class<T> tClass, Base base, LoadStrategy strategy) {
        App.logger.info("Attempting to load from file: " + pathNew);
        T tNew = validateObject(JsonHandler.readValue(pathNew, tClass), tClass, base, true);
        T tOld;
        if (strategy == LoadStrategy.UPDATE) {
            tOld = validateObject(JsonHandler.readValue(pathOld, tClass), tClass, base, false);
        } else {
            tOld = null;
        }

        if (tNew == null) {
            doMove(pathNew, pathFail);
            App.logger.warn("Failed to load file: " + pathNew);
        } else {
            doMove(pathNew, pathOld);
            App.logger.info("Successfully loaded file: " + pathNew);
        }
        return new PreloadPair<>(tNew, tOld);
    }

    private static <T> T validateObject(T object, Class<T> tClass, Base base, boolean printValidationError) {
        try {
            switch (base) {
                case ALIAS:
                    return tClass.cast(CommandValidator.validateAlias((LoadedAlias) object));
                case CMD:
                    return tClass.cast(CommandValidator.validateCommand((LoadedCommand) object));
                case FILTER:
                    return tClass.cast(FilterValidator.validateFilter((BasicFilter) object));
                case FILTER_GRP:
                    return tClass.cast(FilterValidator.validateFilterGroup((FilterGroup) object));
                default:
                    return null;
            }
        } catch (ClassCastException e) {
            App.logger.catching(e);
            return null;
        } catch (InvalidAliasException | InvalidCommandException | InvalidFilterException | InvalidFilterGroupException e) {
            if (printValidationError) {
                App.logger.error(e.getMessage());
            }
            return null;
        }
    }

    private static Class getClassFromBase(Base base) {
        switch (base) {
            case ALIAS:
                return LoadedAlias.class;
            case CMD:
                return LoadedCommand.class;
            case FILTER:
                return BasicFilter.class;
            case FILTER_GRP:
                return FilterGroup.class;
            default:
                return null;
        }
    }

    private static void doMove(String sourcePath, String destPath) {
        if (!move(new File(sourcePath), new File(destPath)) && !sourcePath.equals(destPath)) {
            App.logger.error("Failed to move file '" + sourcePath + "' to file '" + destPath + "'");
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
