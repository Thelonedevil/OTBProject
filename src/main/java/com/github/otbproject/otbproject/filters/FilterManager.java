package com.github.otbproject.otbproject.filters;

import java.util.Map;
import java.util.Set;

public class FilterManager {
    public final Set<Filter> filters;
    public final Map<String, FilterGroup> filterGroups;

    public FilterManager(Set<Filter> filters, Map<String, FilterGroup> filterGroups) {
        this.filters = filters;
        this.filterGroups = filterGroups;
    }
}
