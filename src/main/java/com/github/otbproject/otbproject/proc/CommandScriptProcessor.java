package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.scripts.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

import java.io.File;

public class CommandScriptProcessor {
    private static final String METHOD_NAME = "execute";
    private static final ScriptProcessor PROCESSOR = new ScriptProcessor();

    public static boolean process(String scriptName, DatabaseWrapper db, String commandName, String[] commandArgs, String channel, String destinationChannel, String user, UserLevel userLevel) {
        ScriptArgs args = new ScriptArgs(db, commandName, commandArgs, channel, destinationChannel, user, userLevel);
        return PROCESSOR.process(scriptName, (FSUtil.scriptDir() + File.separator + scriptName), METHOD_NAME, args);
    }
}