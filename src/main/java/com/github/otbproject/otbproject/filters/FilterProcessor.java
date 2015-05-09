package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.scripts.ScriptProcessor;

import java.io.File;
import java.util.regex.Pattern;

public class FilterProcessor {
    public static final String METHOD_NAME = "checkMessage";
    public static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    public static boolean process(Filter filter, String message) {
        if (filter == null) {
            return false;
        }

        switch (filter.getType()) {
            case PLAINTEXT:
                return Pattern.compile(Pattern.quote(filter.getData()), Pattern.CASE_INSENSITIVE).matcher(message).matches();
            case REGEX:
                return Pattern.compile(filter.getData(), Pattern.CASE_INSENSITIVE).matcher(message).matches();
            case SCRIPT:
                // TODO possibly tweak method name and parameter(s) passed in
                return PROCESSOR.process(filter.getData(), (FSUtil.filtersDir() + File.separator + filter.getData()), METHOD_NAME, message);
            // Default should never occur
            default:
                return false;
        }
    }
}
