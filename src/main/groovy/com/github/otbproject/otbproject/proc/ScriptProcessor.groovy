package com.github.otbproject.otbproject.proc

import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.fs.FSUtil
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.users.UserLevel


class ScriptProcessor {
    private static final String METHOD_NAME = "execute";
    public static void process(String path, DatabaseWrapper db, String[] commandArgs, String channel, String user, UserLevel userLevel) {
        Object[] args = [db, commandArgs, channel, user, userLevel];

        String fullPath = FSUtil.SCRIPT_DIR + File.separator + path;

        boolean success = true;
        try {
            success = new GroovyShell().parse(new File(fullPath)).invokeMethod(METHOD_NAME, args);
        } catch (Exception e) {
            App.logger.error("Exception when running command script: " + path);
            App.logger.catching(e);
            success = false;
        }
    }
}
