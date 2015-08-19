package com.github.otbproject.otbproject.util.compat;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.GeneralConfig;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.PathBuilder;
import com.github.otbproject.otbproject.fs.groups.Base;
import com.github.otbproject.otbproject.fs.groups.Chan;
import com.github.otbproject.otbproject.fs.groups.Load;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.github.otbproject.otbproject.util.version.Version;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public class VersionCompatHelper {
    public static void fixCompatIssues(Version oldVersion) {
        if ((oldVersion == null) || App.VERSION.equals(oldVersion)) {
            return;
        }
        if (App.VERSION.checker().major(2).minor(0).isVersion() && oldVersion.checker().major(1).minor(1).isVersion()) {
            fix1_1To2_0();
        }
    }

    private static void fix1_1To2_0() {
        fixScripts();
        fixGeneralConfig();
        removeOldPreloads();
    }

    private static void fixScripts() {
        // Delete scripts from base dir, because they will be unpacked into a subdirectory
        File scriptsDir = new File(FSUtil.scriptDir());
        File[] files = scriptsDir.listFiles();
        if (files == null) {
            return;
        }
        String newDir = FSUtil.commandScriptDir() + File.separator;
        Stream.of(files)
                .filter(File::isFile)
                .forEach(file -> file.renameTo(new File(newDir + file.getName())));
    }

    private static void fixGeneralConfig() {
        Optional<GeneralConfigOld> optional = JsonHandler.readValue((FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG), GeneralConfigOld.class);
        if (!optional.isPresent()) {
            return;
        }
        GeneralConfigOld configOld = optional.get();
        GeneralConfig configNew = new GeneralConfig();

        configNew.setService(configOld.getServiceName());
        configNew.setPermanentlyEnabledCommands(configOld.permanently_enabled_commands);
        JsonHandler.writeValue((FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG), configNew);
    }

    private static void removeOldPreloads() {
        String basePath = new PathBuilder().base(Base.CMD).channels(Chan.ALL).load(Load.ED).create();
        deleteFile(basePath + File.separator + "command.reset.count.success.json");
        deleteFile(FSUtil.commandScriptDir() + File.separator + "ScriptResetCount.groovy");
    }

    private static void deleteFile(String path) {
        if (new File(path).delete()) {
            App.logger.info("Deleted " + path);
        } else {
            App.logger.error("Failed to delete " + path);
        }
    }
}
