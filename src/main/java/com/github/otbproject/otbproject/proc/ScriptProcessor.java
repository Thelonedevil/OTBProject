package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.users.UserLevel;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;

public class ScriptProcessor {
    private static final String METHOD_NAME = "execute";
    private static final GroovyShell SHELL = new GroovyShell();
    private static final ScriptCache CACHE = new ScriptCache();

    public static boolean process(String path, DatabaseWrapper db, String commandName, String[] commandArgs, String channel, String destinationChannel, String user, UserLevel userLevel) {
        ScriptArgs args = new ScriptArgs(db, commandName, commandArgs, channel, destinationChannel, user, userLevel);

        Boolean success;
        try {
            Script script;
            Object scriptReturn;

            // Get script
            if (CACHE.contains(path)) {
                script = CACHE.get(path);
            } else {
                script = SHELL.parse(new File(FSUtil.scriptDir() + File.separator + path));
                CACHE.put(path, script);
            }

            App.logger.info("Running script: " + path);
            scriptReturn = script.invokeMethod(METHOD_NAME, args);
            App.logger.info("Finished running script: " + path);
            if ((scriptReturn == null) || !(scriptReturn instanceof Boolean)) {
                success = true;
            } else {
                success = (Boolean) scriptReturn;
            }
        } catch (Exception e) {
            App.logger.error("Exception when running command script: " + path);
            App.logger.catching(e);
            success = false;
        }

        return success;
    }

    public static void flushScriptCache(String path) {
        CACHE.remove(path);
    }

    public static void clearScriptCache() {
        CACHE.clear();
    }
}
