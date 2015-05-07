package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.scripts.ScriptProcessor;

import java.io.File;

public class FilterProcessor {
    public static final String METHOD_NAME = "checkMessage";
    private static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    public static boolean process(Filter filter, String message) {
        if (filter == null) {
            return false;
        }

        switch (filter.getType()) {
            case PLAINTEXT:
                return message.contains(filter.getData());
            case REGEX:
                return message.matches(filter.getData());
            case SCRIPT:
                // TODO possibly tweak method name and parameter(s) passed in
                return PROCESSOR.process(filter.getData(), (FSUtil.filtersDir() + File.separator + filter.getData()), METHOD_NAME, message);
            // Default should never occur
            default:
                return false;
        }
    }
}
