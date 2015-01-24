package com.github.opentwitchbotteam.otbproject.proc

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper
import com.github.opentwitchbotteam.otbproject.users.UserLevel

class ScriptProcessor {
    static void processScript(String path, DatabaseWrapper db, String[] commandArgs, String channel, String user, UserLevel userLevel) {
        def args = [db, commandArgs, channel, user, userLevel]
        new GroovyShell().parse(new File(path)).invokeMethod(ScriptUtil.METHOD_NAME, args)
    }
}
