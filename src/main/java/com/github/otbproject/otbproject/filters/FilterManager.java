package com.github.otbproject.otbproject.filters;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FilterManager {
    public final ConcurrentHashMap.KeySetView<Filter, Boolean> filters;
    public final ConcurrentMap<String, FilterGroup> filterGroups;

    public FilterManager(ConcurrentHashMap.KeySetView<Filter, Boolean> filters, ConcurrentMap<String, FilterGroup> filterGroups) {
        this.filters = filters;
        this.filterGroups = filterGroups;
    }
}
