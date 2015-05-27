package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;
import java.util.regex.Pattern;

public class Filter {
    private String group;
    private FilterType type;
    private Pattern pattern;
    private String data;
    private boolean enabled;

    public Filter(String group, FilterType type, Pattern pattern, String data, boolean enabled) {
        this.group = group;
        this.type = type;
        this.pattern = pattern;
        this.data = data; // Script if script; original regex string otherwise
        this.enabled = enabled;
    }

    public String getGroup() {
        return group;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean matches(String message) {
        switch (type) {
            case PLAINTEXT:
            case REGEX:
                return pattern.matcher(message).matches();
            case SCRIPT:
                // TODO possibly tweak method name and parameter(s) passed in
                return FilterProcessor.PROCESSOR.process(data, (FSUtil.filtersDir() + File.separator + data), FilterProcessor.METHOD_NAME, message);
            // Default should never occur
            default:
                return false;
        }
    }

    public BasicFilter toBasicFilter() {
        BasicFilter filter = new BasicFilter();
        filter.setGroup(group);
        filter.setType(type);
        filter.setData(data);
        filter.setEnabled(enabled);
        return filter;
    }

    public static Filter fromBasicFilter(BasicFilter filter) {
        Pattern pattern = null;
        switch (filter.getType()) {
            case PLAINTEXT:
                pattern = Pattern.compile(Pattern.quote(filter.getData()), Pattern.CASE_INSENSITIVE);
                break;
            case REGEX:
                pattern = Pattern.compile(filter.getData(), Pattern.CASE_INSENSITIVE);
                break;
        }
        boolean enabled = (filter.isEnabled() == null) ? true : filter.isEnabled();
        return new Filter(filter.getGroup(), filter.getType(), pattern, filter.getData(), enabled);
    }
}
