package com.github.otbproject.otbproject.filters;

public class FilterValidator {
    public static BasicFilter validateFilter(BasicFilter filter) throws InvalidFilterException {
        if ((filter == null) || (filter.getData() == null) || (filter.getType() == null)) {
            throw new InvalidFilterException("Object or required field is null");
        }

        BasicFilter validatedFilter = FilterHelper.getCopy(filter);
        BasicFilter defaultFilter = DefaultFilterGenerator.createDefaultFilter();

        if (validatedFilter.getGroup() == null) {
            validatedFilter.setGroup(defaultFilter.getGroup());
        }
        if (validatedFilter.isEnabled() == null) {
            validatedFilter.setEnabled(defaultFilter.isEnabled());
        }

        return validatedFilter;
    }

    public static FilterGroup validateFilterGroup(FilterGroup group) throws InvalidFilterGroupException {
        if ((group == null) || (group.getName() == null)) {
            throw new InvalidFilterGroupException("Object or required field is null");
        }

        if (group.getName().contains(" ")) {
            throw new InvalidFilterGroupException("Space not allowed in name");
        }

        FilterGroup validatedGroup = FilterHelper.getCopy(group);
        FilterGroup defaultGroup = DefaultFilterGenerator.createDefaultFilterGroup();

        if (validatedGroup.getResponseCommand() == null) {
            validatedGroup.setResponseCommand(defaultGroup.getResponseCommand());
        }
        if (validatedGroup.getUserLevel() == null) {
            validatedGroup.setUserLevel(defaultGroup.getUserLevel());
        }
        if (validatedGroup.getAction() == null) {
            validatedGroup.setAction(defaultGroup.getAction());
        }
        if (validatedGroup.isEnabled() == null) {
            validatedGroup.setEnabled(defaultGroup.isEnabled());
        }

        return validatedGroup;
    }
}
