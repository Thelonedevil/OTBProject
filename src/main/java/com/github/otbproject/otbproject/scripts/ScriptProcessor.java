package com.github.otbproject.otbproject.scripts;

import com.github.otbproject.otbproject.App;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;

public class ScriptProcessor {
    private static final GroovyShell SHELL = new GroovyShell();

    private final ScriptCache cache = new ScriptCache();

    public boolean process(String scriptName, String path, String methodName, Object paramContainer) {
        Boolean success;
        try {
            Script script;
            Object scriptReturn;

            // Get script
            if (cache.contains(scriptName)) {
                script = cache.get(scriptName);
            } else {
                script = SHELL.parse(new File(path));
                cache.put(scriptName, script);
            }

            App.logger.debug("Running script: " + scriptName);
            scriptReturn = script.invokeMethod(methodName, paramContainer);
            App.logger.debug("Finished running script: " + scriptName);
            if ((scriptReturn == null) || !(scriptReturn instanceof Boolean)) {
                App.logger.warn("Missing or invalid return statement in script: " + scriptName);
                success = true;
            } else {
                success = (Boolean) scriptReturn;
            }
        } catch (Exception e) {
            App.logger.error("Exception when running script: " + scriptName);
            App.logger.catching(e);
            success = false;
        }

        App.logger.debug("Script '" + scriptName + "' returned: " + success.toString());
        return success;
    }

    public void flushScriptCache(String scriptName) {
        cache.remove(scriptName);
    }

    public void clearScriptCache() {
        cache.clear();
    }
}
