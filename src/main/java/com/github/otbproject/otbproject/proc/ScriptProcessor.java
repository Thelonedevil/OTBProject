package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.users.UserLevel;
import groovy.lang.GroovyShell;

import java.io.File;

public class ScriptProcessor {
    private static final String METHOD_NAME = "execute";

    public static boolean process(String path, DatabaseWrapper db, String commandName, String[] commandArgs, String channel, String destinationChannel, String user, UserLevel userLevel) {
        Object[] args = new Object[]{db, commandName, commandArgs, channel, destinationChannel, user, userLevel};

        String fullPath = FSUtil.scriptDir() + File.separator + path;

        Boolean success;
        Object scriptReturn;
        try {
            scriptReturn = new GroovyShell().parse(new File(fullPath)).invokeMethod(METHOD_NAME, args);
            if ((scriptReturn == null) || !(scriptReturn instanceof Boolean)) {
                success = true;
            }
            else {
                success = (Boolean) scriptReturn;
            }
        } catch (Exception e) {
            App.logger.error("Exception when running command script: " + path);
            App.logger.catching(e);
            success = false;
        }

        return success;
    }
}
