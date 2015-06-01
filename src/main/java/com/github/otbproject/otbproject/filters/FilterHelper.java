package com.github.otbproject.otbproject.filters;

public class FilterHelper {
    public static BasicFilter getCopy(BasicFilter filter) {
        BasicFilter copy = new BasicFilter();

        copy.setData(filter.getData());
        copy.setType(filter.getType());
        copy.setGroup(filter.getGroup());
        copy.setEnabled(filter.isEnabled());

        return copy;
    }

    public static FilterGroup getCopy(FilterGroup group) {
        FilterGroup copy = new FilterGroup();

        copy.setName(group.getName());
        copy.setResponseCommand(group.getResponseCommand());
        copy.setUserLevel(group.getUserLevel());
        copy.setAction(group.getAction());
        copy.setEnabled(group.isEnabled());

        return copy;
    }
}
