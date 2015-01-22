package com.github.opentwitchbotteam.otbproject.proc

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper
import com.github.opentwitchbotteam.otbproject.users.UserLevel

class ScriptProcessor {
    static void processScript(String path, DatabaseWrapper db, String[] commandArgs, String execChannel, String targetChannel, String user, UserLevel userLevel) {
        def args = [db, commandArgs, execChannel, targetChannel, user, userLevel]
        new GroovyShell().parse(new File(path)).invokeMethod(ScriptUtil.METHOD_NAME, args)
    }
}
