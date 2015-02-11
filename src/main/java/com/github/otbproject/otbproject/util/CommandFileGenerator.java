package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;

public class CommandFileGenerator {
    public static void generateCommandFile() {
        LoadedCommand command = DefaultCommandGenerator.createDefaultCommand();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "example-command.json", command);
    }

    public static void generateAliasFile() {
        LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "example-alias.json", alias);
    }
}
