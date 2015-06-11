package com.github.otbproject.otbproject.script;

import com.github.otbproject.otbproject.App;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;

public class ScriptProcessor<T> {
    private static final GroovyShell SHELL = new GroovyShell();

    private final ScriptCache cache = new ScriptCache();
    private final Class<T> tClass;
    private final T defaultResponse;

    public ScriptProcessor(Class<T> tClass, T defaultResponse) {
        this.tClass = tClass;
        this.defaultResponse = defaultResponse;
    }

    public T process(String scriptName, String path, String methodName, Object paramContainer) {
        T response;
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
            if ((scriptReturn == null) || !(tClass.isInstance(scriptReturn))) {
                App.logger.warn("Missing or invalid return statement in script: " + scriptName);
                response = defaultResponse;
            } else {
                response = tClass.cast(scriptReturn);
            }
        } catch (Exception e) {
            App.logger.error("Exception when running script: " + scriptName);
            App.logger.catching(e);
            response = defaultResponse;
        }

        App.logger.debug("Script '" + scriptName + "' returned: " + response);
        return response;
    }

    public void dropFromScriptCache(String scriptName) {
        cache.remove(scriptName);
    }

    public void clearScriptCache() {
        cache.clear();
    }
}
