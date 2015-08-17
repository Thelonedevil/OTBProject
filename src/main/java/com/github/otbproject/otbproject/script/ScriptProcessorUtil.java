package com.github.otbproject.otbproject.script;

import java.io.File;
import java.util.stream.Stream;

public class ScriptProcessorUtil {
    public static void cacheFromDirectory(ScriptProcessor processor, String directory) {
        File[] files = new File(directory).listFiles();
        if (files == null) {
            return;
        }
        Stream.of(files).filter(File::isFile).forEach(file -> processor.cache(file.getName(), file.getPath()));
    }
}
