package com.github.otbproject.otbproject.filters;

import java.util.ArrayList;
import java.util.Map;

public class FilterManager {
    public final ArrayList<Filter> filters;
    public final Map<String, FilterGroup> filterGroups;

    public FilterManager(ArrayList<Filter> filters, Map<String, FilterGroup> filterGroups) {
        this.filters = filters;
        this.filterGroups = filterGroups;
    }
}
