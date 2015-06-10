package com.github.otbproject.otbproject.filter;

import com.github.otbproject.otbproject.scripts.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FilterProcessor {
    static final String METHOD_NAME = "checkMessage";
    static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    // TODO delete
    public static FilterGroup process(ConcurrentHashMap.KeySetView<Filter, Boolean> filters, ConcurrentMap<String, FilterGroup> filterGroups, String message, UserLevel userLevel) {
        if (userLevel.getValue() < UserLevel.MODERATOR.getValue()) {
            return null;
        }
        Optional<Filter> filterOptional = filters.stream()
                .filter(Filter::isEnabled)
                .filter(filter -> {
                    FilterGroup group = filterGroups.get(filter.getGroup());
                    return (group != null) && (group.isEnabled()) && (group.getUserLevel().getValue() >= userLevel.getValue());
                })
                .filter(filter -> filter.matches(message))
                .findAny();
        if (!filterOptional.isPresent()) {
            return null;
        }
        return filterGroups.get(filterOptional.get().getGroup());
    }

    // Returns the FilterGroup of a Filter which matches the message, or null if no Filter matches
    public static FilterGroup process(ConcurrentMap<String, GroupFilterSet> groupFilterSets, String message, UserLevel userLevel) {
        if (userLevel.getValue() < UserLevel.MODERATOR.getValue()) {
            return null;
        }
        Optional<Map.Entry<String, GroupFilterSet>> entryOptional = groupFilterSets.entrySet().stream()
                .filter(entry -> entry.getValue().group.isEnabled())
                .filter(entry -> entry.getValue().group.getUserLevel().getValue() >= userLevel.getValue())
                .filter(entry -> entry.getValue().filterSet.stream()
                        .filter(Filter::isEnabled)
                        .filter(filter -> filter.matches(message))
                        .findAny().isPresent()
                )
                .findAny();
        if (!entryOptional.isPresent()) {
            return null;
        }
        return entryOptional.get().getValue().group;
    }
}
