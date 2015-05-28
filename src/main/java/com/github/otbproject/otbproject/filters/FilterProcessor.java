package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.scripts.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FilterProcessor {
    static final String METHOD_NAME = "checkMessage";
    static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    // Returns the FilterGroup of a Filter which matches the message, or null if no Filter matches
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
}
