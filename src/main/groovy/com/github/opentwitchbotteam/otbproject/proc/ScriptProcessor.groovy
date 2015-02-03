package com.github.opentwitchbotteam.otbproject.proc

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper
import com.github.opentwitchbotteam.otbproject.users.UserLevel
import org.codehaus.groovy.control.CompilerConfiguration


class ScriptProcessor {
    private static final String BASE_CLASS = "ScriptBase";

    static void processScript(String path, DatabaseWrapper db, String[] commandArgs, String channel, String user, UserLevel userLevel) {

        new OtherScriptProcessor().processScript(path, db, commandArgs, channel, user, userLevel);

        /*
        Map<String, Object> map = new HashMap<>();
        map.put("db", db);
        map.put("args", commandArgs);
        map.put("channel", channel);
        map.put("user", user);
        map.put("userLevel", userLevel);

        def configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(BASE_CLASS);
        Binding binding = new Binding(map)

        def args = [db, commandArgs, channel, user, userLevel]
        new GroovyShell(this.class.classLoader, binding, configuration).parse(new File(path)).invokeMethod(ScriptUtil.METHOD_NAME, args)
        */
    }
}
