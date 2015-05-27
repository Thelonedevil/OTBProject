package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.scripts.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

import java.util.Map;
import java.util.Set;

public class FilterProcessor {
    static final String METHOD_NAME = "checkMessage";
    static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    public static boolean process(Set<Filter> filters, Map<String, FilterGroup> filterGroups, String message, UserLevel userLevel) {
        return (userLevel.getValue() < UserLevel.MODERATOR.getValue())
                && filters.stream()
                .filter(Filter::isEnabled)
                .filter(filter -> {
                    FilterGroup group = filterGroups.get(filter.getGroup());
                    return (group != null) && (group.getUserLevel().getValue() >= userLevel.getValue());
                })
                .filter(filter -> filter.matches(message))
                .findAny().isPresent();
    }
}
