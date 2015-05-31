package com.github.otbproject.otbproject.util.unpack;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.filters.BasicFilter;
import com.github.otbproject.otbproject.filters.FilterGroup;
import com.github.otbproject.otbproject.filters.FilterGroups;
import com.github.otbproject.otbproject.filters.Filters;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.fs.groups.Load;
import com.github.otbproject.otbproject.util.JsonHandler;

import java.io.File;
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
                loadForAllChannels(list, base, strategy);
                break;
            case SPECIFIC:
                loadForChannel(list, channelName, base, strategy);
                break;
            case BOT:
                loadForBotChannel(list, base, strategy);
                break;
        }
    }

    private static void loadForAllChannels(List<PreloadPair> list, Base base, LoadStrategy strategy) {
        File[] files = new File(FSUtil.dataDir() + File.separator + FSUtil.DirNames.CHANNELS).listFiles();
        if (files == null) {
            App.logger.error("Unable to get list of channels");
            return;
        }

        Stream.of(files).filter(File::isDirectory)
                .forEach(file -> loadForChannel(list, file.getName(), base, strategy));
    }

    private static void loadForChannel(List<PreloadPair> list, String channel, Base base, LoadStrategy strategy) {
        DatabaseWrapper db = APIDatabase.getChannelMainDatabase(channel);
        if (db == null) {
            App.logger.error("Unable to get database for channel: " + channel);
            return;
        }
        loadFromList(list, db, base, strategy);
    }

    private static void loadForBotChannel(List<PreloadPair> list, Base base, LoadStrategy strategy) {
        DatabaseWrapper db = APIDatabase.getBotDatabase();
        if (db == null) {
            App.logger.error("Unable to get bot database");
            return;
        }
        loadFromList(list, db, base, strategy);
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
                    Alias.addAliasFromLoadedAlias(db, alias);
                    break;
                case CMD:
                    LoadedCommand command = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateCommandHybrid(db, (LoadedCommand) tNew, (LoadedCommand) tOld) : (LoadedCommand) tNew;
                    Command.addCommandFromLoadedCommand(db, command);
                    break;
                case FILTER:
                    BasicFilter filter = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateFilterHybrid(db, (BasicFilter) tNew, (BasicFilter) tOld) : (BasicFilter) tNew;
                    Filters.addFilterFromObj(db, filter);
                    break;
                case FILTER_GRP:
                    FilterGroup group = (strategy == LoadStrategy.UPDATE) ?
                            PreloadComparator.generateFilterGroupHybrid(db, (FilterGroup) tNew, (FilterGroup) tOld) : (FilterGroup) tNew;
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

        return Stream.of(files)
                .map(file -> {
                    String name = file.getName();
                    String pathNew = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.TO).create() + File.separator + name;
                    String pathOld = FSUtil.builder.base(base).channels(chan).forChannel(channelName).load(Load.ED).create() + File.separator + name;
                    return loadFromFile(pathNew, pathOld, tClass, strategy);
                })
                .collect(Collectors.toList());
    }

    private static <T> PreloadPair<T> loadFromFile(String pathNew, String pathOld, Class<T> tClass, LoadStrategy strategy) {
        T tNew = JsonHandler.readValue(pathNew, tClass);
        T tOld;
        switch (strategy) {
            case UPDATE:
                tOld = JsonHandler.readValue(pathOld, tClass);
                break;
            default:
                tOld = null;
        }
        doMove(pathNew, pathOld);
        return new PreloadPair<>(tNew, tOld);
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
        if (!move(new File(sourcePath), new File(destPath))) {
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

    public static void test() {
        File dir = FSUtil.builder.base(Base.CMD).channels(Chan.ALL).load(Load.ED).asFile();
        File[] files = dir.listFiles();
        if (files != null) {
            Stream.of(files).forEach(file -> App.logger.info(file.getName()));
        }
        System.exit(0);
    }
}
