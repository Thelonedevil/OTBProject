package com.github.opentwitchbotteam.otbproject.proc;

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;
import com.github.opentwitchbotteam.otbproject.users.UserLevel;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;

public class OtherScriptProcessor {
    public void processScript(String path, DatabaseWrapper db, String[] commandArgs, String channel, String user, UserLevel userLevel) {
        try {
            ClassLoader parent = getClass().getClassLoader();
            GroovyClassLoader loader = new GroovyClassLoader(parent);
            Class groovyClass;

            groovyClass = loader.parseClass(new File(path));

            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            Object[] args = {db, commandArgs, channel, user, userLevel};

            groovyObject.invokeMethod(ScriptUtil.METHOD_NAME, args);


        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
