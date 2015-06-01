package com.github.otbproject.otbproject.util.unpack;

import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.filters.BasicFilter;
import com.github.otbproject.otbproject.filters.FilterGroup;

class PreloadComparator {
    static LoadedAlias generateAliasHybrid(DatabaseWrapper db, LoadedAlias newAlias, LoadedAlias oldAlias) {
        if ((oldAlias == null) || !oldAlias.getName().equals(newAlias.getName())) {
            return newAlias;
        }
    }

    static LoadedCommand generateCommandHybrid(DatabaseWrapper db, LoadedCommand newCommand, LoadedCommand oldCommand) {
        if ((oldCommand == null) || !oldCommand.getName().equals(newCommand.getName())) {
            return newCommand;
        }
    }

    static BasicFilter generateFilterHybrid(DatabaseWrapper db, BasicFilter newFilter, BasicFilter oldFilter) {
        if ((oldFilter == null) || !(oldFilter.getData().equals(newFilter.getData()) && oldFilter.getType().equals(newFilter.getType()))) {
            return newFilter;
        }
    }

    static FilterGroup generateFilterGroupHybrid(DatabaseWrapper db, FilterGroup newGroup, FilterGroup oldGroup) {
        if ((oldGroup == null) || !oldGroup.getName().equals(newGroup.getName())) {
            return newGroup;
        }
    }
}
