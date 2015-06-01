package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.users.UserLevel;

public class DefaultFilterGenerator {
    // TODO change to only create one instance ever
    public static BasicFilter createDefaultFilter() {
        BasicFilter filter = new BasicFilter();
        filter.setData("default data");
        filter.setType(FilterType.PLAINTEXT);
        filter.setGroup("default"); // requires the existence of a FilterGroup with the name "default"
        filter.setEnabled(true);

        return filter;
    }

    public static FilterGroup createDefaultFilterGroup() {
        FilterGroup group = new FilterGroup();
        group.setName("default");
        group.setResponseCommand("~%filter.response.default"); // TODO create default response command
        group.setUserLevel(UserLevel.SUBSCRIBER);
        group.setAction(FilterAction.PURGE);
        group.setEnabled(true);

        return group;
    }
}
