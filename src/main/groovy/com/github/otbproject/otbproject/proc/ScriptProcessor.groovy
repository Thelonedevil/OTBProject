package com.github.otbproject.otbproject.proc

import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.fs.FSUtil
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.users.UserLevel


class ScriptProcessor {
    private static final String METHOD_NAME = "execute";
    public static void process(String path, DatabaseWrapper db, String[] commandArgs, String channel, String user, UserLevel userLevel) {

        Map<String, Object> map = new HashMap<>();
        map.put("db", db);
        map.put("args", commandArgs);
        map.put("channel", channel);
        map.put("user", user);
        map.put("userLevel", userLevel);

        Binding binding = new Binding(map);

        String fullPath = FSUtil.SCRIPT_DIR + File.separator + path;

        boolean success = true;
        try {
            success = new GroovyShell(this.class.classLoader, binding).parse(new File(fullPath)).invokeMethod(METHOD_NAME, null);
        } catch (Exception e) {
            App.logger.catching(e);
            success = false;
        }
    }
}
