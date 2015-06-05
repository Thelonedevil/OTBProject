package com.github.otbproject.otbproject.util.preload;

import com.github.otbproject.otbproject.commands.Aliases;
import com.github.otbproject.otbproject.commands.Commands;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.filters.BasicFilter;
import com.github.otbproject.otbproject.filters.FilterGroup;
import com.github.otbproject.otbproject.filters.FilterGroups;
import com.github.otbproject.otbproject.filters.Filters;

class PreloadComparator {
    static LoadedAlias generateAliasHybrid(DatabaseWrapper db, LoadedAlias newAlias, LoadedAlias oldAlias) {
        if ((oldAlias == null) || (newAlias == null) || !oldAlias.getName().equals(newAlias.getName())) {
            return newAlias;
        }

        LoadedAlias dbAlias = Aliases.get(db, newAlias.getName());
        if (dbAlias == null) {
            return newAlias;
        }

        // Check all alias fields
        if (!oldAlias.getCommand().equals(dbAlias.getCommand())) {
            newAlias.setCommand(dbAlias.getCommand());
        }
        if (!oldAlias.getModifyingUserLevel().equals(dbAlias.getModifyingUserLevel())) {
            newAlias.setModifyingUserLevel(dbAlias.getModifyingUserLevel());
        }
        if (!oldAlias.isEnabled().equals(dbAlias.isEnabled())) {
            newAlias.setEnabled(dbAlias.isEnabled());
        }

        return newAlias;
    }

    static LoadedCommand generateCommandHybrid(DatabaseWrapper db, LoadedCommand newCommand, LoadedCommand oldCommand) {
        if ((oldCommand == null) || (newCommand == null) || !oldCommand.getName().equals(newCommand.getName())) {
            return newCommand;
        }

        LoadedCommand dbCommand = Commands.get(db, newCommand.getName());
        if (dbCommand == null) {
            return newCommand;
        }

        // Check all command fields
        if (!oldCommand.getResponse().equals(dbCommand.getResponse())) {
            newCommand.setResponse(dbCommand.getResponse());
        }
        if (!oldCommand.getExecUserLevel().equals(dbCommand.getExecUserLevel())) {
            newCommand.setExecUserLevel(dbCommand.getExecUserLevel());
        }
        if (oldCommand.getMinArgs() != dbCommand.getMinArgs()) {
            newCommand.setMinArgs(dbCommand.getMinArgs());
        }
        // Skip count
        if (!oldCommand.modifyingUserLevels.getNameModifyingUL().equals(dbCommand.modifyingUserLevels.getNameModifyingUL())) {
            newCommand.modifyingUserLevels.setNameModifyingUL(dbCommand.modifyingUserLevels.getNameModifyingUL());
        }
        if (!oldCommand.modifyingUserLevels.getResponseModifyingUL().equals(dbCommand.modifyingUserLevels.getResponseModifyingUL())) {
            newCommand.modifyingUserLevels.setResponseModifyingUL(dbCommand.modifyingUserLevels.getResponseModifyingUL());
        }
        if (!oldCommand.modifyingUserLevels.getUserLevelModifyingUL().equals(dbCommand.modifyingUserLevels.getUserLevelModifyingUL())) {
            newCommand.modifyingUserLevels.setUserLevelModifyingUL(dbCommand.modifyingUserLevels.getUserLevelModifyingUL());
        }
        // Handle script
        if (    ((oldCommand.getScript() == null) && (dbCommand.getScript() != null))
                || ((oldCommand.getScript() != null) && !oldCommand.getScript().equals(dbCommand.getScript()))  ) {
            newCommand.setScript(dbCommand.getScript());
        }
        if (!oldCommand.isEnabled().equals(dbCommand.isEnabled())) {
            newCommand.setEnabled(dbCommand.isEnabled());
        }
        if (oldCommand.isDebug() != dbCommand.isDebug()) {
            newCommand.setDebug(dbCommand.isDebug());
        }

        return newCommand;
    }

    static BasicFilter generateFilterHybrid(DatabaseWrapper db, BasicFilter newFilter, BasicFilter oldFilter) {
        if ((oldFilter == null) || (newFilter == null)
                || !(oldFilter.getData().equals(newFilter.getData()) && oldFilter.getType().equals(newFilter.getType()))) {
            return newFilter;
        }

        BasicFilter dbFilter = Filters.get(db, newFilter.getData(), newFilter.getType());
        if (dbFilter == null) {
            return newFilter;
        }

        // Check all filter fields
        if (!oldFilter.getGroup().equals(dbFilter.getGroup())) {
            newFilter.setGroup(dbFilter.getGroup());
        }
        if (!oldFilter.isEnabled().equals(dbFilter.isEnabled())) {
            newFilter.setEnabled(dbFilter.isEnabled());
        }

        return newFilter;
    }

    static FilterGroup generateFilterGroupHybrid(DatabaseWrapper db, FilterGroup newGroup, FilterGroup oldGroup) {
        if ((oldGroup == null) || (newGroup == null) || !oldGroup.getName().equals(newGroup.getName())) {
            return newGroup;
        }

        FilterGroup dbGroup = FilterGroups.get(db, newGroup.getName());
        if (dbGroup == null) {
            return newGroup;
        }

        // Check all filter group fields
        if (!oldGroup.getResponseCommand().equals(dbGroup.getResponseCommand())) {
            newGroup.setResponseCommand(dbGroup.getResponseCommand());
        }
        if (!oldGroup.getUserLevel().equals(dbGroup.getUserLevel())) {
            newGroup.setUserLevel(dbGroup.getUserLevel());
        }
        if (!oldGroup.getAction().equals(dbGroup.getAction())) {
            newGroup.setAction(dbGroup.getAction());
        }
        if (!oldGroup.isEnabled().equals(dbGroup.isEnabled())) {
            newGroup.setEnabled(dbGroup.isEnabled());
        }

        return newGroup;
    }
}
