package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.scripts.ScriptProcessor;

import java.io.File;
import java.util.regex.Pattern;

public class Filter {
    public static final String METHOD_NAME = "checkMessage";
    private static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    private String group;
    private FilterType type;
    private Pattern pattern;
    private String data;

    public Filter(String group, FilterType type, Pattern pattern, String data) {
        this.group = group;
        this.type = type;
        this.pattern = pattern;
        this.data = data; // Script if script; original regex string otherwise
    }

    public String getGroup() {
        return group;
    }

    public boolean matches(String message) {
        switch (type) {
            case PLAINTEXT:
            case REGEX:
                return pattern.matcher(message).matches();
            case SCRIPT:
                // TODO possibly tweak method name and parameter(s) passed in
                return PROCESSOR.process(data, (FSUtil.filtersDir() + File.separator + data), METHOD_NAME, message);
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
        return new Filter(filter.getGroup(), filter.getType(), pattern, filter.getData());
    }
}
